package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class GroovyAppE2ETest extends BootAppE2ETest {

    @Test
    void groovyBootAppSupportsTemplatesInjectionBeanModelDataAndActions() {
        navigateTo("/groovy.html");

        assertThat(page.locator("#headline")).hasText("Groovy E2E");
        assertThat(page.locator("#injected-message")).hasText("constructor-injection-ok");
        assertThat(page.locator("#bean-label")).hasText("bean-label-from-groovy-bean");
        assertThat(page.locator("#groovy-items li")).hasText(new String[]{
                "default-template",
                "getter-modeldata",
                "form-action"
        });
        assertThat(page.locator("#saved")).hasText("nothing-saved");

        page.locator("#groovy-name").fill("Ada");
        page.locator("#groovy-amount").fill("7");
        page.locator("#groovy-save").click();
        assertThat(page.locator("#saved")).hasText("Ada:7");
    }

    @Test
    void groovyBootAppSupportsExplicitHtmlFileNextToGroovyController() {
        navigateTo("/groovy.html");

        page.locator("#explicit-link").click();
        assertThat(page.locator("#explicit-message")).hasText("explicit-htmlfile-ok");

        page.locator("#home-link").click();
        assertThat(page.locator("#headline")).hasText("Groovy E2E");
    }
}
