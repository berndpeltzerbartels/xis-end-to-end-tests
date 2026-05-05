package app;

import one.xis.auth.AccessTokenClaims;
import one.xis.auth.IDPClientInfo;
import one.xis.auth.IDPClientInfoImpl;
import one.xis.auth.IDPService;
import one.xis.auth.IDPUserInfo;
import one.xis.auth.IDPUserInfoImpl;
import one.xis.auth.IDTokenClaims;
import one.xis.context.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class XisE2EIDPService implements IDPService {

    private static final String USER_ID = "xis-idp-user";
    private static final String PASSWORD = "secret";
    private static final String CLIENT_ID = "xis-e2e-client";
    private static final String CLIENT_SECRET = "xis-e2e-secret";

    @Override
    public Optional<IDPUserInfo> userInfo(String userId) {
        if (!USER_ID.equals(userId)) {
            return Optional.empty();
        }
        return Optional.of(new IDPUserInfoImpl(userId, CLIENT_ID));
    }

    @Override
    public Optional<AccessTokenClaims> accessTokenClaims(String userId) {
        if (!USER_ID.equals(userId)) {
            return Optional.empty();
        }
        var claims = new AccessTokenClaims();
        claims.setUsername(userId);
        claims.setRoles(List.of("USER"));
        return Optional.of(claims);
    }

    @Override
    public Optional<IDTokenClaims> idTokenClaims(String userId) {
        if (!USER_ID.equals(userId)) {
            return Optional.empty();
        }
        var claims = new IDTokenClaims();
        claims.setPreferredUsername(userId);
        claims.setEmail("xis-idp-user@example.test");
        claims.setEmailVerified(true);
        return Optional.of(claims);
    }

    @Override
    public Optional<IDPClientInfo> findClientInfo(String clientId) {
        if (!CLIENT_ID.equals(clientId)) {
            return Optional.empty();
        }
        return Optional.of(new IDPClientInfoImpl(CLIENT_ID, CLIENT_SECRET, Set.of(redirectUri())));
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        return USER_ID.equals(username) && PASSWORD.equals(password);
    }

    @Override
    public boolean validateClientSecret(String clientId, String clientSecret) {
        return CLIENT_ID.equals(clientId) && CLIENT_SECRET.equals(clientSecret);
    }

    private String redirectUri() {
        return System.getProperty("e2e.idp.client.redirect.uri");
    }
}
