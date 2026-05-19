package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class KotlinAppE2ETest extends BootAppE2ETest {

    @Test
    void kotlinBootAppSupportsTemplatesInjectionBeanModelDataAndActions() {
        navigateTo("/kotlin.html");

        assertThat(page.locator("#headline")).hasText("Kotlin E2E");
        assertThat(page.locator("#injected-message")).hasText("constructor-injection-ok");
        assertThat(page.locator("#bean-label")).hasText("bean-label-from-kotlin-bean");
        assertThat(page.locator("#kotlin-items li")).hasText(new String[]{
                "default-template",
                "getter-modeldata",
                "form-action"
        });
        assertThat(page.locator("#saved")).hasText("nothing-saved");

        page.locator("#kotlin-name").fill("Ada");
        page.locator("#kotlin-amount").fill("7");
        page.locator("#kotlin-save").click();
        assertThat(page.locator("#saved")).hasText("Ada:7");
    }

    @Test
    void kotlinBootAppSupportsExplicitHtmlFileNextToKotlinController() {
        navigateTo("/kotlin.html");

        page.locator("#explicit-link").click();
        assertThat(page.locator("#explicit-message")).hasText("explicit-htmlfile-ok");

        page.locator("#home-link").click();
        assertThat(page.locator("#headline")).hasText("Kotlin E2E");
    }
}
