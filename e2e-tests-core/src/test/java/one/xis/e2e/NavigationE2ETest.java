package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests basic page rendering and navigation between pages.
 */
class NavigationE2ETest extends BootAppE2ETest {

    @Test
    void indexPageLoads() {
        navigateTo("/");
        assertThat(page.locator("#title")).hasText("XIS E2E Test App");
    }

    @Test
    void navigateFromIndexToNavigation() {
        navigateTo("/");
        page.locator("#nav-navigation").click();
        page.waitForLoadState();
        assertThat(page.locator("#step")).hasText("navigation-page");
    }

    @Test
    void navigateFromIndexToCounter() {
        navigateTo("/");
        page.locator("#nav-counter").click();
        page.waitForLoadState();
        assertThat(page.locator("#count")).hasText("0");
    }

    @Test
    void navigateFromIndexToForm() {
        navigateTo("/");
        page.locator("#nav-form").click();
        page.waitForLoadState();
        assertThat(page.locator("#input-name")).isVisible();
    }

    @Test
    void multiStepNavigation() {
        // Index → Navigation → Counter → Home
        navigateTo("/");
        page.locator("#nav-navigation").click();
        page.waitForLoadState();
        assertThat(page.locator("#step")).hasText("navigation-page");

        page.locator("#btn-go-counter").click();
        page.waitForLoadState();
        assertThat(page.locator("#count")).hasText("0");

        page.locator("#nav-home").click();
        page.waitForLoadState();
        assertThat(page.locator("#title")).hasText("XIS E2E Test App");
    }
}
