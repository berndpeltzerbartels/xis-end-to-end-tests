package one.xis.e2e.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.security.mode", matches = "xis-idp")
class XisIdpSecurityE2ETest extends SecurityAppE2ETest {

    @Test
    void unauthenticatedUserCanLoginThroughXisOpenIdConnectProvider() {
        navigateTo("/protected.html");

        loginAtXisIdp();

        page.waitForURL(baseUrl + "/protected.html");
        assertThat(page.locator("#protected-title")).hasText("Protected");
        assertThat(page.locator("#protected-message")).hasText("Protected content for xis-idp-user");
    }

}
