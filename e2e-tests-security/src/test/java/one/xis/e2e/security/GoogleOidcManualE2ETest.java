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
        navigateTo("/protected.html");

        page.locator("a[href*='/xis/auth/callback/google']").click();
        page.waitForURL("https://accounts.google.com/**");

        /*
         * Continue manually in the headed browser. After Google redirects back,
         * XIS stores a local application token with roles from
         * GoogleSecurityUserInfoService.
         */
        page.waitForURL(baseUrl + "/protected.html");
        assertThat(page.locator("#protected-title")).hasText("Protected");
        assertThat(page.locator("#protected-message")).containsText("Protected content for ");
    }
}
