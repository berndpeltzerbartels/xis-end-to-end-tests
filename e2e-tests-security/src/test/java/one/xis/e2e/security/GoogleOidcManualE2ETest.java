package one.xis.e2e.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Manual smoke test for Google OpenID Connect.
 * <p>
 * This test is disabled by default because it needs real Google OAuth client
 * credentials and a human/browser login. Do not commit credentials. To run it
 * locally, configure a Google web OAuth client with redirect URI
 * {@code http://localhost:<port>/xis/auth/callback/google} and enable the test
 * locally.
 * <p>
 * It is kept as executable documentation for the real-provider scenario. The
 * automated OIDC behavior is covered by {@link ExternalOidcSecurityE2ETest} and
 * {@link KeycloakSecurityE2ETest}.
 */
@EnabledIfSystemProperty(named = "e2e.google.enabled", matches = "true")
class GoogleOidcManualE2ETest extends SecurityAppE2ETest {

    @Test
    void userCanLoginThroughGoogleOpenIdConnectProviderWhenEnabledLocally() {
        navigateTo("/community.html");

        if (!page.url().startsWith("https://accounts.google.com/")) {
            page.locator("a[href*='/xis/auth/callback/google']").click();
        }
        page.waitForURL("https://accounts.google.com/**");

        /*
         * Continue manually in the headed browser. After Google redirects back,
         * XIS stores a local application token. The target page uses @Roles
         * without named roles, so the Google user only needs to be
         * authenticated.
         */
        page.waitForURL(baseUrl + "/community.html");
        assertThat(page.locator("#community-title")).hasText("Community");
        assertThat(page.locator("#community-message")).containsText("Community content for ");
    }
}
