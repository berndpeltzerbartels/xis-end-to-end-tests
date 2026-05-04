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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

/**
 * Base class for all E2E tests against a runnable XIS platform app.
 * <p>
 * Starts the platform fat-jar once per test class as a local process,
 * opens a headless Chromium via Playwright for each test method.
 * <p>
 * The fat-jar path and port argument format are provided by Gradle.
 */
public abstract class BootAppE2ETest {

    // --- Application process (once per test class) --------------------------

    protected static Process appProcess;
    protected static String baseUrl;

    @BeforeAll
    static void startApplication() {
        String jarPath = System.getProperty("e2e.app.jar");
        if (jarPath == null) {
            throw new IllegalStateException(
                "System property 'e2e.app.jar' not set. " +
                "Run via Gradle: ./gradlew :e2e-tests-core:test");
        }
        String portArgumentFormat = System.getProperty("e2e.app.portArgument", "%d");

        int port = findFreePort();
        baseUrl = "http://127.0.0.1:" + port;

        try {
            appProcess = new ProcessBuilder("java", "-jar", jarPath, portArgumentFormat.formatted(port))
                .inheritIO()
                .start();
            waitForConfig();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start XIS E2E app", e);
        }
    }

    @AfterAll
    static void stopApplication() {
        if (appProcess != null) {
            appProcess.destroy();
        }
    }

    // --- Playwright (once per JVM, shared across test classes) --------------

    private static Playwright playwright;
    private static Browser browser;

    @BeforeAll
    static void startBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser    != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // --- Per-test page context -----------------------------------------------

    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void openPage() {
        context = browser.newContext();
        page    = context.newPage();
    }

    @AfterEach
    void closePage() {
        context.close();
    }

    // --- Helpers -------------------------------------------------------------

    protected void navigateTo(String path) {
        page.navigate(baseUrl + path);
        page.waitForLoadState();
    }

    private static int findFreePort() {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find a free port", e);
        }
    }

    private static void waitForConfig() {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create(baseUrl + "/xis/config")).GET().build();
        var deadline = Instant.now().plusSeconds(60);

        while (Instant.now().isBefore(deadline)) {
            if (!appProcess.isAlive()) {
                throw new IllegalStateException("XIS E2E app exited before it became ready");
            }
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() == 200) {
                    return;
                }
                if (isTransientGatewayStatus(response.statusCode())) {
                    continue;
                }
                throw new IllegalStateException(
                    "XIS E2E app responded on /xis/config with status " + response.statusCode());
            } catch (IOException | InterruptedException ignored) {
                if (ignored instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for XIS E2E app", ignored);
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for XIS E2E app", e);
            }
        }

        throw new IllegalStateException("XIS E2E app did not become ready at " + baseUrl);
    }

    private static boolean isTransientGatewayStatus(int statusCode) {
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }

}
