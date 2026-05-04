package one.xis.e2e.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.security.mode", matches = "local")
class LocalSecurityE2ETest extends SecurityAppE2ETest {

    @Test
    void unauthenticatedUserIsRedirectedToLoginAndCanContinueAfterLocalLogin() {
        navigateTo("/protected.html");

        assertThat(page.locator("#login-title")).hasText("Custom Login");
        assertThat(page.locator("#username")).isVisible();

        login("alice", "secret", "/protected.html");

        assertThat(page.locator("#protected-title")).hasText("Protected");
        assertThat(page.locator("#protected-message")).hasText("Protected content for alice");
    }

    @Test
    void missingControllerRoleRedirectsLoggedInUserToLogin() {
        navigateTo("/protected.html");
        login("alice", "secret", "/protected.html");

        navigateTo("/admin.html");

        assertThat(page.locator("#username")).isVisible();
    }

    @Test
    void dtoRoleIsRequiredForProtectedFormAction() {
        navigateTo("/editor.html");
        login("alice", "secret", "/editor.html");

        page.locator("#editor-value").fill("changed by alice");
        page.locator("#editor-save").click();
        page.waitForLoadState();

        assertThat(page.locator("#username")).isVisible();
    }

    @Test
    void userWithDtoRoleCanSubmitProtectedFormAction() {
        navigateTo("/editor.html");
        login("editor", "secret", "/editor.html");

        page.locator("#editor-value").fill("published");
        page.locator("#editor-save").click();
        page.waitForLoadState();

        assertThat(page.locator("#editor-title")).hasText("Editor");
        assertThat(page.locator("#saved-value")).hasText("published");
    }
}
