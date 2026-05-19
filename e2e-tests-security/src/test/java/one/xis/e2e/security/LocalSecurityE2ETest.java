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
    void totpUserCanLoginWithValidCode() {
        String secret = provisionTotpSecret("totpAlice");

        navigateTo("/protected.html");

        assertThat(page.locator("#totpCode")).isVisible();
        loginWithTotp("totpAlice", "secret", currentTotpCode(secret), "/protected.html");

        assertThat(page.locator("#protected-title")).hasText("Protected");
        assertThat(page.locator("#protected-message")).hasText("Protected content for totpAlice");
    }

    @Test
    void totpSetupFlowCreatesSecretThatCanBeUsedForLogin() {
        navigateTo("/totp-setup.html");

        setupTotp("totpSetupFlow");

        assertThat(page.locator("img[src^='data:image/svg+xml']")).hasCount(1);
        assertThat(page.locator("body")).containsText("totpSetupFlow");

        String secret = provisionTotpSecret("totpSetupFlow");
        navigateTo("/protected.html");

        loginWithTotp("totpSetupFlow", "secret", currentTotpCode(secret), "/protected.html");

        assertThat(page.locator("#protected-title")).hasText("Protected");
        assertThat(page.locator("#protected-message")).hasText("Protected content for totpSetupFlow");
    }

    @Test
    void totpUserCannotLoginWithInvalidCode() {
        provisionTotpSecret("totpEditor");

        navigateTo("/protected.html");
        page.waitForFunction("window.XIS !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill("totpEditor");
        page.locator("#password").fill("secret");
        page.locator("#totpCode").fill("000000");
        page.locator("#login-button").click();
        page.waitForLoadState();

        assertThat(page.locator("#login-title")).hasText("Custom Login");
        assertThat(page.locator("#username")).isVisible();
    }

    @Test
    void totpUserCannotLoginWithValidCodeAndInvalidPassword() {
        String secret = provisionTotpSecret("totpWrongPassword");

        navigateTo("/protected.html");
        page.waitForFunction("window.XIS !== undefined && document.querySelector('#username') !== null");
        page.locator("#username").fill("totpWrongPassword");
        page.locator("#password").fill("wrong");
        page.locator("#totpCode").fill(currentTotpCode(secret));
        page.locator("#login-button").click();
        page.waitForLoadState();

        assertThat(page.locator("#login-title")).hasText("Custom Login");
        assertThat(page.locator("#username")).isVisible();
    }

    @Test
    void totpSetupQrCodeIsOnlyKeptForCurrentActionResult() {
        navigateTo("/totp-setup.html");
        assertThat(page.locator("img[src^='data:image/svg+xml']")).hasCount(0);

        setupTotp("totpSetupAlice");

        assertThat(page.locator("img[src^='data:image/svg+xml']")).hasCount(1);
        assertThat(page.locator("body")).containsText("totpSetupAlice");

        page.reload();
        page.waitForLoadState();

        assertThat(page.locator("img[src^='data:image/svg+xml']")).hasCount(0);
        assertThat(page.locator("body")).not().containsText("totpSetupAlice");

        setupTotp("totpSetupBob");

        assertThat(page.locator("img[src^='data:image/svg+xml']")).hasCount(1);
        assertThat(page.locator("body")).containsText("totpSetupBob");
        assertThat(page.locator("body")).not().containsText("totpSetupAlice");
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

    @Test
    void ownershipGuardRejectsForeignSubmittedObject() {
        navigateTo("/editor.html");
        login("editor", "secret", "/editor.html");

        page.locator("#editor-document-id").fill("article-alice");
        page.locator("#editor-value").fill("forged");
        page.locator("#editor-save").click();
        page.waitForLoadState();

        assertThat(page.locator("#username")).isVisible();
    }

    private void setupTotp(String username) {
        page.waitForFunction("window.XIS !== undefined && document.querySelector('#totp-setup-username') !== null");
        page.locator("#totp-setup-username").fill(username);
        page.locator("#totp-setup-password").fill("secret");
        page.locator("#totp-setup-button").click();
        page.waitForLoadState();
    }
}
