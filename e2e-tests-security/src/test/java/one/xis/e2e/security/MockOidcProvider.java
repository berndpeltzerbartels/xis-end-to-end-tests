package one.xis.e2e.security;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

final class MockOidcProvider implements AutoCloseable {

    private static final String KEY_ID = "mock-key";
    private final HttpServer server;
    private final KeyPair keyPair;
    private final String issuer;

    private MockOidcProvider(HttpServer server, KeyPair keyPair) {
        this.server = server;
        this.keyPair = keyPair;
        this.issuer = "http://localhost:" + server.getAddress().getPort();
    }

    static MockOidcProvider start() {
        try {
            var keyPair = createKeyPair();
            var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            var provider = new MockOidcProvider(server, keyPair);
            provider.createContexts();
            server.start();
            return provider;
        } catch (Exception e) {
            throw new RuntimeException("Failed to start mock OIDC provider", e);
        }
    }

    String getIssuer() {
        return issuer;
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private void createContexts() {
        server.createContext("/.well-known/openid-configuration", this::wellKnown);
        server.createContext("/authorize", this::authorize);
        server.createContext("/token", this::token);
        server.createContext("/userinfo", this::userInfo);
        server.createContext("/jwks", this::jwks);
    }

    private void wellKnown(HttpExchange exchange) throws IOException {
        writeJson(exchange, 200, """
                {
                  "issuer": "%s",
                  "authorization_endpoint": "%s/authorize",
                  "token_endpoint": "%s/token",
                  "userinfo_endpoint": "%s/userinfo",
                  "jwks_uri": "%s/jwks"
                }
                """.formatted(issuer, issuer, issuer, issuer, issuer));
    }

    private void authorize(HttpExchange exchange) throws IOException {
        var query = query(exchange.getRequestURI());
        var redirectUri = query.get("redirect_uri");
        var state = query.get("state");
        exchange.getResponseHeaders().add("Location", redirectUri + "?code=mock-code&state=" + state);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private void token(HttpExchange exchange) throws IOException {
        var accessToken = token(accessClaims());
        var refreshToken = token(refreshClaims());
        var idToken = token(idClaims());
        writeJson(exchange, 200, """
                {
                  "access_token": "%s",
                  "refresh_token": "%s",
                  "id_token": "%s",
                  "expires_in": 300,
                  "refresh_expires_in": 1800,
                  "token_type": "Bearer"
                }
                """.formatted(accessToken, refreshToken, idToken));
    }

    private void userInfo(HttpExchange exchange) throws IOException {
        writeJson(exchange, 200, """
                {
                  "sub": "oidc-user",
                  "preferred_username": "oidc-user",
                  "email": "oidc-user@example.test",
                  "email_verified": true
                }
                """);
    }

    private void jwks(HttpExchange exchange) throws IOException {
        var publicKey = (RSAPublicKey) keyPair.getPublic();
        writeJson(exchange, 200, """
                {
                  "keys": [
                    {
                      "kty": "RSA",
                      "alg": "RS256",
                      "use": "sig",
                      "kid": "%s",
                      "n": "%s",
                      "e": "%s"
                    }
                  ]
                }
                """.formatted(KEY_ID, base64(publicKey.getModulus()), base64(publicKey.getPublicExponent())));
    }

    private String accessClaims() {
        long now = Instant.now().getEpochSecond();
        return """
                {
                  "sub": "oidc-user",
                  "iss": "%s",
                  "exp": %d,
                  "iat": %d,
                  "nbf": %d,
                  "client_id": "xis-e2e-client",
                  "username": "oidc-user",
                  "realm_access": {"roles": ["USER"]},
                  "resource_access": {"account": {"roles": ["USER"]}}
                }
                """.formatted(issuer, now + 300, now, now);
    }

    private String refreshClaims() {
        long now = Instant.now().getEpochSecond();
        return """
                {
                  "sub": "oidc-user",
                  "iss": "%s",
                  "exp": %d,
                  "iat": %d,
                  "nbf": %d,
                  "client_id": "xis-e2e-client"
                }
                """.formatted(issuer, now + 1800, now, now);
    }

    private String idClaims() {
        long now = Instant.now().getEpochSecond();
        return """
                {
                  "sub": "oidc-user",
                  "iss": "%s",
                  "exp": %d,
                  "iat": %d,
                  "nbf": %d,
                  "client_id": "xis-e2e-client",
                  "preferred_username": "oidc-user",
                  "email": "oidc-user@example.test",
                  "email_verified": true
                }
                """.formatted(issuer, now + 300, now, now);
    }

    private String token(String claimsJson) {
        var header = """
                {"alg":"RS256","typ":"JWT","kid":"%s"}""".formatted(KEY_ID);
        var headerAndPayload = base64(header.getBytes(StandardCharsets.UTF_8)) + "." + base64(claimsJson.getBytes(StandardCharsets.UTF_8));
        return headerAndPayload + "." + sign(headerAndPayload);
    }

    private String sign(String data) {
        try {
            var signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            return base64(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign mock OIDC token", e);
        }
    }

    private static KeyPair createKeyPair() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static Map<String, String> query(URI uri) {
        var result = new HashMap<String, String>();
        var query = uri.getRawQuery();
        if (query == null || query.isBlank()) {
            return result;
        }
        for (String parameter : query.split("&")) {
            var parts = parameter.split("=", 2);
            var key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            var value = parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    private static void writeJson(HttpExchange exchange, int status, String json) throws IOException {
        var bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String base64(BigInteger value) {
        return base64(value.toByteArray());
    }

    private static String base64(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
