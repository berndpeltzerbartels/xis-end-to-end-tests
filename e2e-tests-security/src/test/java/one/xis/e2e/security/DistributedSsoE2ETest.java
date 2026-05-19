package one.xis.e2e.security;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.distributed.sso.enabled", matches = "true")
class DistributedSsoE2ETest {

    private static Process shellProcess;
    private static Process remoteProcess;
    private static Process idpProcess;
    private static String shellBaseUrl;
    private static String remoteBaseUrl;
    private static String idpBaseUrl;
    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void startApplications() throws IOException {
        int shellPort = findFreePort();
        int remotePort = findFreePort();
        int idpPort = findFreePort();
        shellBaseUrl = "http://localhost:" + shellPort;
        remoteBaseUrl = "http://localhost:" + remotePort;
        idpBaseUrl = "http://localhost:" + idpPort;

        idpProcess = startJar(
                requiredProperty("e2e.idp.jar"),
                System.getProperty("e2e.idp.portArgument", "%d"),
                idpPort,
                "-De2e.idp.client.redirect.uri=" + shellBaseUrl + "/xis/auth/callback/xis-idp"
        );
        waitForConfig(idpProcess, idpBaseUrl);

        shellProcess = startDistributedApp(
                requiredProperty("e2e.distributed.sso.shell.jar"),
                System.getProperty("e2e.distributed.sso.shell.portArgument", "%d"),
                shellPort
        );
        remoteProcess = startDistributedApp(
                requiredProperty("e2e.distributed.sso.remote.jar"),
                System.getProperty("e2e.distributed.sso.remote.portArgument", "%d"),
                remotePort
        );
        waitForConfig(shellProcess, shellBaseUrl);
        waitForConfig(remoteProcess, remoteBaseUrl);

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void stopApplications() {
        if (shellProcess != null) shellProcess.destroy();
        if (remoteProcess != null) remoteProcess.destroy();
        if (idpProcess != null) idpProcess.destroy();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    void xisIdpLoginAuthenticatesRemotePageAndRemoteSseRefreshesSameUser() {
        BrowserContext firstContext = browser.newContext();
        BrowserContext secondContext = browser.newContext();
        try {
            Page firstPage = firstContext.newPage();
            Page secondPage = secondContext.newPage();

            openRemotePageThroughShellLogin(firstPage);
            assertThat(firstPage.locator("#distributed-sso-user")).hasText("xis-idp-user");
            assertThat(firstPage.locator("#distributed-sso-version")).hasText("0");

            openRemotePageThroughShellLogin(secondPage);
            secondPage.locator("#publish-distributed-sso-event").click();

            assertThat(firstPage.locator("#distributed-sso-version")).hasText("1");
        } finally {
            firstContext.close();
            secondContext.close();
        }
    }

    private void openRemotePageThroughShellLogin(Page page) {
        page.navigate(shellBaseUrl + "/distributed-sso-shell.html");
        page.waitForURL(idpBaseUrl + "/idp/login.html**");
        page.waitForFunction("window.XIS !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill("xis-idp-user");
        page.locator("#password").fill("secret");
        page.locator("button[type='submit']").click();
        page.waitForURL(shellBaseUrl + "/distributed-sso-shell.html");
        page.waitForFunction("window.XIS !== undefined && document.querySelector('#distributed-sso-shell-title') !== null");
        page.locator("#open-distributed-sso-remote").click();
        assertThat(page.locator("#distributed-sso-remote-title")).hasText("Distributed SSO Remote");
    }

    private static Process startDistributedApp(String jarPath, String portArgumentFormat, int port) throws IOException {
        return startJar(
                jarPath,
                portArgumentFormat,
                port,
                "-De2e.xis.idp.url=" + idpBaseUrl,
                "-De2e.distributed.sso.shellHost=" + shellBaseUrl,
                "-De2e.distributed.sso.remoteHost=" + remoteBaseUrl
        );
    }

    private static Process startJar(String jarPath, String portArgumentFormat, int port, String... systemProperties) throws IOException {
        var command = new ArrayList<String>();
        command.add("java");
        for (String systemProperty : systemProperties) {
            command.add(systemProperty);
        }
        command.add("-jar");
        command.add(jarPath);
        command.add(portArgumentFormat.formatted(port));
        return new ProcessBuilder(command)
                .inheritIO()
                .start();
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
                throw new IllegalStateException("XIS distributed SSO E2E process exited before it became ready");
            }
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 200) {
                    return;
                }
                if (!isTransientGatewayStatus(response.statusCode())) {
                    throw new IllegalStateException("XIS distributed SSO E2E process responded on /xis/config with status " + response.statusCode());
                }
            } catch (IOException | InterruptedException ignored) {
                if (ignored instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for XIS distributed SSO E2E app", ignored);
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for XIS distributed SSO E2E app", e);
            }
        }

        throw new IllegalStateException("XIS distributed SSO E2E process did not become ready at " + url);
    }

    private static boolean isTransientGatewayStatus(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }
}
