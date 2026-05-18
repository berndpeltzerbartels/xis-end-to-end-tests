package one.xis.e2e;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class BootHttpDependencyE2ETest extends BootAppE2ETest {

    @Test
    void xisBootHttpDependencyStartsPlainHttpControllerApplication() throws Exception {
        var response = get("/api/probe?name=Gradle");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("boot-http:Gradle");
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
