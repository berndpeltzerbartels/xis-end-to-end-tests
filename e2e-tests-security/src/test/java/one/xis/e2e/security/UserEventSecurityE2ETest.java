package one.xis.e2e.security;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.security.mode", matches = "local")
class UserEventSecurityE2ETest extends SecurityAppE2ETest {

    @Test
    void refreshEventToUserUpdatesAllClientsOfThatUserOnly() {
        BrowserContext aliceContext = browser.newContext();
        BrowserContext editorContext = browser.newContext();
        try {
            Page aliceOtherPage = loginAndOpenUserEvents(aliceContext, "alice");
            Page editorPage = loginAndOpenUserEvents(editorContext, "editor");
            loginAndOpenUserEvents(page, "alice");

            resetUserEvent(page);
            resetUserEvent(editorPage);

            assertThat(page.locator("#user-event-version")).hasText("0");
            assertThat(aliceOtherPage.locator("#user-event-version")).hasText("0");
            assertThat(editorPage.locator("#user-event-version")).hasText("0");

            page.locator("#publish-user-event").click();

            assertThat(page.locator("#user-event-version")).hasText("1");
            assertThat(aliceOtherPage.locator("#user-event-version")).hasText("1");
            assertThat(editorPage.locator("#user-event-version")).hasText("0");
        } finally {
            aliceContext.close();
            editorContext.close();
        }
    }

    @Test
    void refreshEventToAllUsersUpdatesEveryAuthenticatedClient() {
        BrowserContext aliceContext = browser.newContext();
        BrowserContext editorContext = browser.newContext();
        try {
            Page aliceOtherPage = loginAndOpenUserEvents(aliceContext, "alice");
            Page editorPage = loginAndOpenUserEvents(editorContext, "editor");
            loginAndOpenUserEvents(page, "alice");

            resetAllUsersEvent(page);

            assertThat(page.locator("#all-users-event-version")).hasText("0");
            assertThat(aliceOtherPage.locator("#all-users-event-version")).hasText("0");
            assertThat(editorPage.locator("#all-users-event-version")).hasText("0");

            page.locator("#publish-all-users-event").click();

            assertThat(page.locator("#all-users-event-version")).hasText("1");
            assertThat(aliceOtherPage.locator("#all-users-event-version")).hasText("1");
            assertThat(editorPage.locator("#all-users-event-version")).hasText("1");
        } finally {
            aliceContext.close();
            editorContext.close();
        }
    }

    private Page loginAndOpenUserEvents(BrowserContext context, String username) {
        Page targetPage = context.newPage();
        return loginAndOpenUserEvents(targetPage, username);
    }

    private Page loginAndOpenUserEvents(Page targetPage, String username) {
        targetPage.navigate(baseUrl + "/user-events.html");
        targetPage.waitForFunction("window.XIS !== undefined && document.querySelector('#username') !== null");
        targetPage.locator("#username").fill(username);
        targetPage.locator("#password").fill("secret");
        targetPage.locator("#login-button").click();
        targetPage.waitForURL(baseUrl + "/user-events.html");
        targetPage.waitForLoadState();
        targetPage.waitForFunction("window.XIS && window.XIS.isEventStreamConnected()");
        assertThat(targetPage.locator("#user-event-user")).hasText(username);
        return targetPage;
    }

    private void resetUserEvent(Page targetPage) {
        targetPage.locator("#reset-user-event").click();
        assertThat(targetPage.locator("#user-event-version")).hasText("0");
    }

    private void resetAllUsersEvent(Page targetPage) {
        targetPage.locator("#reset-all-users-event").click();
        assertThat(targetPage.locator("#all-users-event-version")).hasText("0");
    }
}
