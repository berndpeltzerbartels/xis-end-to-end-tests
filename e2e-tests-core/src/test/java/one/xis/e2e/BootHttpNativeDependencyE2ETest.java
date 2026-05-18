package one.xis.e2e;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class BootHttpNativeDependencyE2ETest {

    @Test
    void xisBootHttpDependencyWorksInNativeBootApplication() throws Exception {
        String executable = System.getProperty("e2e.native.bootHttp.executable");
        assertThat(executable).as("e2e.native.bootHttp.executable").isNotBlank();

        int port = findFreePort();
        File logFile = new File("build/boot-http-native-e2e.log");
        logFile.getParentFile().mkdirs();
        Process process = new ProcessBuilder(executable, String.valueOf(port))
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.to(logFile))
                .start();
        try {
            waitForServer(process, port, logFile);
            var response = get("http://127.0.0.1:" + port + "/api/probe?name=Native");

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body()).isEqualTo("boot-http-native:Native");
        } finally {
            process.destroy();
            if (!process.waitFor(3, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                process.waitFor();
            }
        }
    }

    private static HttpResponse<String> get(String url) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void waitForServer(Process process, int port, File logFile) throws InterruptedException {
        var deadline = Instant.now().plusSeconds(30);
        while (Instant.now().isBefore(deadline)) {
            if (!process.isAlive()) {
                throw new IllegalStateException("Native boot-http app exited before it became ready. Log:\n" + readLog(logFile));
            }
            try {
                var response = get("http://127.0.0.1:" + port + "/api/probe?name=ready");
                if (response.statusCode() == 200) {
                    return;
                }
            } catch (IOException ignored) {
                Thread.sleep(250);
            }
        }
        throw new IllegalStateException("Native boot-http app did not become ready. Log:\n" + readLog(logFile));
    }

    private static String readLog(File logFile) {
        try {
            return java.nio.file.Files.readString(logFile.toPath());
        } catch (IOException e) {
            return "<log unavailable>";
        }
    }

    private static int findFreePort() {
        try (var socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find a free port", e);
        }
    }
}
