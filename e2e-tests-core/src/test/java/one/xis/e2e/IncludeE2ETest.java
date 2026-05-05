package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class IncludeE2ETest extends BootAppE2ETest {

    @Test
    void rendersIncludeTagAndAttributeSyntax() {
        openIncludes();

        assertThat(page.locator("#tag-include #include-navigation")).isVisible();
        assertThat(page.locator("#attribute-include #include-summary")).isVisible();
        assertThat(page.locator("#include-title")).hasText("Reusable navigation");
        assertThat(page.locator("#include-count")).hasText("0");
    }

    @Test
    void initializesHandlersInsideIncludedMarkup() {
        openIncludes();

        page.locator("#include-action").click();
        assertThat(page.locator("#include-count")).hasText("1");

        page.locator("#include-counter-link").click();
        assertThat(page.locator("#count")).hasText("0");
    }

    private void openIncludes() {
        navigateTo("/includes.html");
        page.locator("#include-reset").click();
        assertThat(page.locator("#include-count")).hasText("0");
    }
}
