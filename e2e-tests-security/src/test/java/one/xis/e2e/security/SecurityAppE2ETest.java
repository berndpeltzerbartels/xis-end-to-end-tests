package one.xis.e2e.security;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

abstract class SecurityAppE2ETest {

    protected static Process appProcess;
    protected static Process idpProcess;
    protected static String baseUrl;
    protected static String idpBaseUrl;
    private static MockOidcProvider mockOidcProvider;

    private static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void startApplication() {
        String jarPath = System.getProperty("e2e.app.jar");
        if (jarPath == null) {
            throw new IllegalStateException("System property 'e2e.app.jar' not set.");
        }
        String portArgumentFormat = System.getProperty("e2e.app.portArgument", "%d");
        int port = configuredPort("e2e.app.port").orElseGet(SecurityAppE2ETest::findFreePort);
        baseUrl = "http://localhost:" + port;
        int idpPort = findFreePort();
        idpBaseUrl = "http://localhost:" + idpPort;

        try {
            if (usesXisIdp()) {
                startXisIdp(idpPort);
            }
            if (usesMockOidc()) {
                mockOidcProvider = MockOidcProvider.start();
            }

            var command = new ArrayList<String>();
            command.add("java");
            if (usesMockOidc()) {
                command.add("-De2e.oidc.url=" + mockOidcProvider.getIssuer());
            }
            if (usesXisIdp()) {
                command.add("-De2e.xis.idp.url=" + idpBaseUrl);
            }
            command.add("-jar");
            command.add(jarPath);
            command.add(portArgumentFormat.formatted(port));
            appProcess = new ProcessBuilder(command)
                    .inheritIO()
                    .start();
            waitForConfig(appProcess, baseUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start XIS security E2E app", e);
        }
    }

    @AfterAll
    static void stopApplication() {
        if (appProcess != null) {
            appProcess.destroy();
        }
        if (idpProcess != null) {
            idpProcess.destroy();
            idpProcess = null;
        }
        if (mockOidcProvider != null) {
            mockOidcProvider.close();
            mockOidcProvider = null;
        }
    }

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("e2e.browser.headless", "true"));
        var launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
        String channel = System.getProperty("e2e.browser.channel");
        if (channel != null && !channel.isBlank()) {
            launchOptions.setChannel(channel);
        }
        browser = playwright.chromium().launch(launchOptions);
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void openPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closePage() {
        context.close();
    }

    protected void navigateTo(String path) {
        page.navigate(baseUrl + path);
        page.waitForLoadState();
    }

    protected void login(String username, String password, String expectedPath) {
        page.waitForFunction("window.app !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("#login-button").click();
        page.waitForURL(baseUrl + expectedPath);
        page.waitForLoadState();
    }

    protected void loginWithTotp(String username, String password, String totpCode, String expectedPath) {
        page.waitForFunction("window.app !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("#totpCode").fill(totpCode);
        page.locator("#login-button").click();
        page.waitForURL(baseUrl + expectedPath);
        page.waitForLoadState();
    }

    protected String provisionTotpSecret(String userId) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(baseUrl + "/e2e/totp/provisioning-uri?userId=" + userId)).GET().build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("TOTP provisioning endpoint returned " + response.statusCode());
            }
            return secret(response.body());
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Unable to provision TOTP secret", e);
        }
    }

    protected String currentTotpCode(String secret) {
        return totpCode(secret, System.currentTimeMillis() / 1000 / 30);
    }

    protected void loginAtXisIdp() {
        page.waitForURL(idpBaseUrl + "/idp/login.html**");
        page.waitForFunction("window.app !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill("xis-idp-user");
        page.locator("#password").fill("secret");
        page.locator("button[type='submit']").click();
    }

    protected static boolean isLocalMode() {
        return "local".equals(System.getProperty("e2e.security.mode", "local"));
    }

    protected static boolean isExternalMode() {
        return "external".equals(System.getProperty("e2e.security.mode", "local"));
    }

    protected static boolean isExternalUserInfoMode() {
        return "external-userinfo".equals(System.getProperty("e2e.security.mode", "local"));
    }

    protected static boolean isXisIdpMode() {
        return "xis-idp".equals(System.getProperty("e2e.security.mode", "local"));
    }

    protected static boolean isMultipleIdpMode() {
        return "multiple-idp".equals(System.getProperty("e2e.security.mode", "local"));
    }

    private static boolean usesMockOidc() {
        return isExternalMode() || isExternalUserInfoMode() || isMultipleIdpMode();
    }

    private static boolean usesXisIdp() {
        return isXisIdpMode() || isMultipleIdpMode();
    }

    private static void startXisIdp(int port) throws IOException {
        String jarPath = System.getProperty("e2e.idp.jar");
        if (jarPath == null) {
            throw new IllegalStateException("System property 'e2e.idp.jar' not set.");
        }
        String portArgumentFormat = System.getProperty("e2e.idp.portArgument", "%d");
        var command = new ArrayList<String>();
        command.add("java");
        command.add("-De2e.idp.client.redirect.uri=" + baseUrl + "/xis/auth/callback/xis-idp");
        command.add("-jar");
        command.add(jarPath);
        command.add(portArgumentFormat.formatted(port));
        idpProcess = new ProcessBuilder(command)
                .inheritIO()
                .start();
        waitForConfig(idpProcess, idpBaseUrl);
    }

    private static int findFreePort() {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find a free port", e);
        }
    }

    private static java.util.Optional<Integer> configuredPort(String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(Integer.parseInt(value));
    }

    private static void waitForConfig(Process process, String url) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(url + "/xis/config")).GET().build();
        var deadline = Instant.now().plusSeconds(60);

        while (Instant.now().isBefore(deadline)) {
            if (!process.isAlive()) {
                throw new IllegalStateException("XIS security E2E process exited before it became ready");
            }
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 200) {
                    return;
                }
                if (!isTransientGatewayStatus(response.statusCode())) {
                    throw new IllegalStateException("XIS security E2E process responded on /xis/config with status " + response.statusCode());
                }
            } catch (IOException | InterruptedException ignored) {
                if (ignored instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for XIS security E2E app", ignored);
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for XIS security E2E app", e);
            }
        }

        throw new IllegalStateException("XIS security E2E process did not become ready at " + url);
    }

    private static boolean isTransientGatewayStatus(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    private static String secret(String provisioningUri) {
        for (String parameter : provisioningUri.substring(provisioningUri.indexOf('?') + 1).split("&")) {
            String[] pair = parameter.split("=", 2);
            if ("secret".equals(pair[0])) {
                return pair[1];
            }
        }
        throw new IllegalArgumentException("No secret in provisioning URI");
    }

    private static String totpCode(String base32Secret, long timeStep) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(base32Decode(base32Secret), "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(Long.BYTES).putLong(timeStep).array());
            int offset = hash[hash.length - 1] & 0x0f;
            int binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);
            return String.format("%06d", binary % 1_000_000);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create TOTP code", e);
        }
    }

    private static byte[] base32Decode(String text) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int buffer = 0;
        int bitsLeft = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = Character.toUpperCase(text.charAt(i));
            int value = c >= 'A' && c <= 'Z' ? c - 'A' : c - '2' + 26;
            buffer = (buffer << 5) | value;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}
