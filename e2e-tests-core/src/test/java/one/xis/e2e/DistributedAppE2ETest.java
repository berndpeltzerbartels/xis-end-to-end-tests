package one.xis.e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

@EnabledIfSystemProperty(named = "e2e.distributed.enabled", matches = "true")
abstract class DistributedAppE2ETest {

    protected static String pageBaseUrl;
    protected static String remoteBaseUrl;

    private static Process pageProcess;
    private static Process remoteProcess;
    private static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void startApplications() {
        String pageJar = requiredProperty("e2e.distributed.page.jar");
        String remoteJar = requiredProperty("e2e.distributed.remote.jar");
        String pagePortArgumentFormat = System.getProperty("e2e.distributed.page.portArgument", "%d");
        String remotePortArgumentFormat = System.getProperty("e2e.distributed.remote.portArgument", "%d");

        int pagePort = findFreePort();
        int remotePort = findFreePort();
        pageBaseUrl = "http://127.0.0.1:" + pagePort;
        remoteBaseUrl = "http://127.0.0.1:" + remotePort;

        remoteProcess = startProcess(remoteJar, remotePortArgumentFormat, remotePort, "remote", pageBaseUrl, remoteBaseUrl);
        waitForConfig(remoteProcess, remoteBaseUrl);

        pageProcess = startProcess(pageJar, pagePortArgumentFormat, pagePort, "page", pageBaseUrl, remoteBaseUrl);
        waitForConfig(pageProcess, pageBaseUrl);

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void stopApplications() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        if (pageProcess != null) pageProcess.destroy();
        if (remoteProcess != null) remoteProcess.destroy();
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

    private static Process startProcess(String jarPath,
                                        String portArgumentFormat,
                                        int port,
                                        String role,
                                        String pageHost,
                                        String remoteHost) {
        try {
            return new ProcessBuilder(
                    "java",
                    "-De2e.distributed.role=" + role,
                    "-De2e.distributed.pageHost=" + pageHost,
                    "-De2e.distributed.remoteHost=" + remoteHost,
                    "-jar",
                    jarPath,
                    portArgumentFormat.formatted(port))
                    .inheritIO()
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start distributed XIS app " + role, e);
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

    private static void waitForConfig(Process process, String baseUrl) {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(baseUrl + "/xis/config")).GET().build();
        var deadline = Instant.now().plusSeconds(60);

        while (Instant.now().isBefore(deadline)) {
            if (!process.isAlive()) {
                throw new IllegalStateException("Distributed XIS app exited before it became ready at " + baseUrl);
            }
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 200) {
                    return;
                }
                if (!isTransientGatewayStatus(response.statusCode())) {
                    throw new IllegalStateException(
                            "Distributed XIS app responded on /xis/config with status " + response.statusCode());
                }
            } catch (IOException | InterruptedException ignored) {
                if (ignored instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for distributed XIS app", ignored);
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for distributed XIS app", e);
            }
        }

        throw new IllegalStateException("Distributed XIS app did not become ready at " + baseUrl);
    }

    private static boolean isTransientGatewayStatus(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }
}
