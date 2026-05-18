package one.xis.e2e;

import com.microsoft.playwright.options.FilePayload;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class UploadE2ETest extends BootAppE2ETest {

    @Test
    void formUploadSendsFileToBackend() {
        navigateTo("/upload.html");

        page.locator("#upload-title").fill("contract");
        page.locator("#upload-file").setInputFiles(new FilePayload(
                "contract.txt",
                "text/plain",
                "Signed upload".getBytes(StandardCharsets.UTF_8)
        ));
        page.locator("#upload-save").click();

        assertThat(page.locator("#upload-result")).hasText("contract:contract.txt:Signed upload");
    }
}
