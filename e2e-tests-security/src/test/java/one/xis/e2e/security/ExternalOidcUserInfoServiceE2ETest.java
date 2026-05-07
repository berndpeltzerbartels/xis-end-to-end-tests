package one.xis.e2e.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@EnabledIfSystemProperty(named = "e2e.security.mode", matches = "external-userinfo")
class ExternalOidcUserInfoServiceE2ETest extends SecurityAppE2ETest {

    @Test
    void externalLoginCallsUserInfoServiceAndUsesMappedLocalRoles() {
        navigateTo("/mapped.html");

        page.waitForURL(baseUrl + "/mapped.html");
        assertThat(page.locator("#mapped-title")).hasText("Mapped");
        assertThat(page.locator("#mapped-message")).hasText("Mapped external account for oidc-user");
    }
}
