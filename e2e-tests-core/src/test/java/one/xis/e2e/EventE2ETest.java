package one.xis.e2e;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class EventE2ETest extends BootAppE2ETest {

    @Test
    void frontletResponseReloadsNamedFrontletInAnotherContainer() {
        openEvents(page);
        assertThat(page.locator("#reload-target-frontlet")).isVisible();
        assertThat(page.locator("#target-render-count")).hasText("1");
        page.locator("#reload-target-frontlet").click();
        assertThat(page.locator("#target-render-count")).hasText("2");
    }

    @Test
    void refreshEventToAllUpdatesOtherClients() {
        BrowserContext otherContext = browser.newContext();
        try {
            Page otherPage = otherContext.newPage();
            openEvents(page);
            page.locator("#reset-events").click();
            assertThat(page.locator("#event-version")).hasText("0");

            openEvents(otherPage);
            assertThat(otherPage.locator("#event-version")).hasText("0");

            page.locator("#publish-all").click();

            assertThat(page.locator("#event-version")).hasText("1");
            assertThat(otherPage.locator("#event-version")).hasText("1");
        } finally {
            otherContext.close();
        }
    }

    @Test
    void refreshEventToClientDoesNotUpdateOtherClientsOfSamePage() {
        BrowserContext otherContext = browser.newContext();
        try {
            Page otherPage = otherContext.newPage();
            openEvents(page);
            page.locator("#reset-events").click();
            assertThat(page.locator("#event-version")).hasText("0");

            openEvents(otherPage);
            assertThat(otherPage.locator("#event-version")).hasText("0");

            page.locator("#publish-client").click();

            assertThat(page.locator("#event-version")).hasText("1");
            assertThat(otherPage.locator("#event-version")).hasText("0");
        } finally {
            otherContext.close();
        }
    }

    private void openEvents(Page targetPage) {
        targetPage.navigate(baseUrl + "/events.html");
        targetPage.waitForLoadState();
        targetPage.waitForFunction("window.app && window.app.eventConnector && window.app.eventConnector.isConnected()");
    }
}
