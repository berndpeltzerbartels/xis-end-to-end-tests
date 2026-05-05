package one.xis.e2e.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.security.mode", matches = "multiple-idp")
class MultipleIdpSecurityE2ETest extends SecurityAppE2ETest {

    @Test
    void userCanChooseXisIdpWhenMultipleOpenIdConnectProvidersAreConfigured() {
        navigateTo("/protected.html");

        assertThat(page.locator("#idp-xis-idp")).isVisible();
        assertThat(page.locator("#idp-mock-oidc")).isVisible();

        page.locator("#idp-xis-idp").click();
        loginAtXisIdp();

        page.waitForURL(baseUrl + "/protected.html");
        assertThat(page.locator("#protected-message")).hasText("Protected content for xis-idp-user");
    }

    @Test
    void userCanChooseMockOpenIdConnectProviderWhenMultipleProvidersAreConfigured() {
        navigateTo("/protected.html");

        assertThat(page.locator("#idp-xis-idp")).isVisible();
        assertThat(page.locator("#idp-mock-oidc")).isVisible();

        page.locator("#idp-mock-oidc").click();

        page.waitForURL(baseUrl + "/protected.html");
        assertThat(page.locator("#protected-message")).hasText("Protected content for oidc-user");
    }
}
