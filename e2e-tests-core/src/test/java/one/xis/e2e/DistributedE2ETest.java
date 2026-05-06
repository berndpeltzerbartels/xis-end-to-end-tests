package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class DistributedE2ETest extends DistributedAppE2ETest {

    @Test
    void shellPageLoadsRemoteFrontletFromRemoteServer() {
        page.navigate(pageBaseUrl + "/distributed-shell.html");
        page.waitForLoadState();

        assertThat(page.locator("#shell-server")).hasText("page");
        assertThat(page.locator("#distributed-remote-frontlet")).isVisible();
        assertThat(page.locator("#frontlet-server")).hasText("remote");
        assertThat(page.locator("#frontlet-count")).hasText("0");

        page.locator("#increment-frontlet").click();

        assertThat(page.locator("#frontlet-server")).hasText("remote");
        assertThat(page.locator("#frontlet-count")).hasText("1");
    }

    @Test
    void shellPageNavigatesToRemotePageAndRunsActionsOnRemoteServer() {
        page.navigate(pageBaseUrl + "/distributed-shell.html");
        page.waitForLoadState();

        page.locator("#open-remote-page").click();

        assertThat(page.locator("#distributed-remote-title")).hasText("Distributed Remote Page");
        assertThat(page.locator("#remote-page-server")).hasText("remote");
        assertThat(page.locator("#remote-page-count")).hasText("0");

        page.locator("#increment-remote-page").click();

        assertThat(page.locator("#remote-page-server")).hasText("remote");
        assertThat(page.locator("#remote-page-count")).hasText("1");
    }
}
