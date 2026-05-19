package one.xis.e2e.security;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.keycloak.enabled", matches = "true")
class KeycloakSecurityE2ETest {

    private static final String KEYCLOAK_CONTAINER = "xis-e2e-keycloak";
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.7";

    private static Process appProcess;
    private static Process colimaTunnel;
    private static String baseUrl;
    private static String keycloakRealmUrl;
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void startApplication() throws IOException, InterruptedException, URISyntaxException {
        int appPort = findFreePort();
        int keycloakPort = findFreePort();
        baseUrl = "http://localhost:" + appPort;
        keycloakRealmUrl = "http://localhost:" + keycloakPort + "/realms/xis";

        startKeycloak(keycloakPort);
        waitForKeycloak(keycloakPort);

        var command = new ArrayList<String>();
        command.add("java");
        command.add("-De2e.oidc.url=" + keycloakRealmUrl);
        command.add("-De2e.oidc.scope=openid roles");
        command.add("-jar");
        command.add(requiredProperty("e2e.app.jar"));
        command.add(System.getProperty("e2e.app.portArgument", "%d").formatted(appPort));
        appProcess = new ProcessBuilder(command)
                .inheritIO()
                .start();
        waitForConfig(appProcess, baseUrl);

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void stopApplication() {
        if (appProcess != null) appProcess.destroy();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        if (colimaTunnel != null) colimaTunnel.destroy();
        runIgnoringFailure(List.of("docker", "rm", "-f", KEYCLOAK_CONTAINER));
    }

    @Test
    void unauthenticatedUserCanLoginThroughRealKeycloakContainer() throws IOException {
        BrowserContext context = browser.newContext();
        try {
            Page page = context.newPage();
            page.navigate(baseUrl + "/protected.html");
            page.waitForURL(keycloakRealmUrl + "/protocol/openid-connect/auth**");
            page.waitForLoadState();

            try {
                page.locator("#username").fill("keycloak-user");
            } catch (PlaywrightException e) {
                throw new AssertionError("Expected Keycloak login page, but current URL is "
                        + page.url() + " and body is:\n" + page.locator("body").innerText(), e);
            }
            page.locator("#password").fill("secret");
            page.locator("#kc-login").click();

            try {
                page.waitForURL(baseUrl + "/protected.html");
                page.waitForLoadState();
            } catch (PlaywrightException e) {
                throw new AssertionError("Expected callback to return to protected page, but current URL is "
                        + page.url() + " and body is:\n" + page.locator("body").innerText(), e);
            }
            try {
                page.waitForFunction("window.XIS !== undefined && document.querySelector('#protected-title') !== null");
            } catch (PlaywrightException e) {
                throw new AssertionError("Expected protected page content after callback, but current URL is "
                        + page.url() + ", content is:\n" + page.content(), e);
            }
            assertThat(page.locator("#protected-title")).hasText("Protected");
            org.assertj.core.api.Assertions.assertThat(page.locator("#protected-message").innerText())
                    .startsWith("Protected content for ")
                    .isNotEqualTo("Protected content for ");
        } finally {
            context.close();
        }
    }

    private static void startKeycloak(int keycloakPort) throws IOException, InterruptedException, URISyntaxException {
        runIgnoringFailure(List.of("docker", "rm", "-f", KEYCLOAK_CONTAINER));
        Path realm = Path.of(KeycloakSecurityE2ETest.class.getClassLoader()
                .getResource("keycloak/xis-realm.json")
                .toURI());
        runRequired(List.of(
                "docker", "run", "-d",
                "--name", KEYCLOAK_CONTAINER,
                "-p", keycloakPort + ":8080",
                "-v", realm + ":/opt/keycloak/data/import/xis-realm.json:ro",
                KEYCLOAK_IMAGE,
                "start-dev", "--import-realm", "--hostname-strict=false"
        ));
        startColimaTunnelIfNeeded(keycloakPort);
    }

    private static void startColimaTunnelIfNeeded(int keycloakPort) throws IOException {
        if (isReachable("http://localhost:" + keycloakPort + "/realms/xis/.well-known/openid-configuration", Duration.ofSeconds(1))) {
            return;
        }
        Path sshConfig = Path.of(System.getProperty("user.home"), ".colima", "_lima", "colima", "ssh.config");
        if (!sshConfig.toFile().exists()) {
            return;
        }
        colimaTunnel = new ProcessBuilder(
                "ssh", "-F", sshConfig.toString(),
                "-S", "none",
                "-o", "ExitOnForwardFailure=yes",
                "-N",
                "-L", "127.0.0.1:" + keycloakPort + ":localhost:" + keycloakPort,
                "lima-colima"
        ).inheritIO().start();
        sleep();
        if (!colimaTunnel.isAlive()) {
            throw new IllegalStateException("Colima SSH tunnel for Keycloak exited before the test could use it");
        }
    }

    private static void waitForKeycloak(int keycloakPort) {
        var url = "http://localhost:" + keycloakPort + "/realms/xis/.well-known/openid-configuration";
        var deadline = Instant.now().plusSeconds(120);
        while (Instant.now().isBefore(deadline)) {
            if (isReachable(url, Duration.ofSeconds(2))) {
                return;
            }
            sleep();
        }
        runIgnoringFailure(List.of("docker", "logs", KEYCLOAK_CONTAINER));
        throw new IllegalStateException("Keycloak did not become ready at " + url);
    }

    private static boolean isReachable(String url, Duration timeout) {
        try {
            var request = HttpRequest.newBuilder(URI.create(url)).timeout(timeout).GET().build();
            var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    private static void runRequired(List<String> command) throws IOException, InterruptedException {
        var exitCode = new ProcessBuilder(command).inheritIO().start().waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("Command failed with exit code " + exitCode + ": " + String.join(" ", command));
        }
    }

    private static void runIgnoringFailure(List<String> command) {
        try {
            new ProcessBuilder(command).inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ignored) {
            if (ignored instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("System property '" + name + "' not set.");
        }
        return value;
    }

    private static int findFreePort() {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find a free port", e);
        }
    }

    private static void waitForConfig(Process process, String url) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(url + "/xis/config")).GET().build();
        var deadline = Instant.now().plusSeconds(60);

        while (Instant.now().isBefore(deadline)) {
            if (!process.isAlive()) {
                throw new IllegalStateException("XIS Keycloak E2E process exited before it became ready");
            }
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 200) {
                    return;
                }
                if (!isTransientGatewayStatus(response.statusCode())) {
                    throw new IllegalStateException("XIS Keycloak E2E process responded on /xis/config with status " + response.statusCode());
                }
            } catch (IOException | InterruptedException ignored) {
                if (ignored instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for XIS Keycloak E2E app", ignored);
                }
            }
            sleep();
        }

        throw new IllegalStateException("XIS Keycloak E2E process did not become ready at " + url);
    }

    private static boolean isTransientGatewayStatus(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

    private static void sleep() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting", e);
        }
    }
}
