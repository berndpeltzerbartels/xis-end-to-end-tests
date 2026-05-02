package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests frontlet loading, actions within frontlets and switching between frontlets.
 */
class FrontletE2ETest extends BootAppE2ETest {

    @Test
    void defaultFrontletLoads() {
        navigateTo("/frontlets.html");
        assertThat(page.locator("#frontlet-a")).isVisible();
        assertThat(page.locator("#label")).hasText("Frontlet A");
    }

    @Test
    void clickInFrontlet() {
        navigateTo("/frontlets.html");
        assertThat(page.locator("#clicks")).hasText("0");
        page.locator("#btn-click").click();
        assertThat(page.locator("#clicks")).hasText("1");
    }

    @Test
    void multipleClicksInFrontlet() {
        navigateTo("/frontlets.html");
        page.locator("#btn-click").click();
        page.locator("#btn-click").click();
        page.locator("#btn-click").click();
        assertThat(page.locator("#clicks")).hasText("3");
    }

    @Test
    void switchFromFrontletAToFrontletB() {
        navigateTo("/frontlets.html");
        assertThat(page.locator("#frontlet-a")).isVisible();

        page.locator("#btn-switch-to-b").click();
        assertThat(page.locator("#frontlet-b")).isVisible();
        assertThat(page.locator("#label")).hasText("Frontlet B");
    }

    @Test
    void switchBackAndForth() {
        navigateTo("/frontlets.html");

        page.locator("#btn-switch-to-b").click();
        assertThat(page.locator("#frontlet-b")).isVisible();

        page.locator("#btn-switch-to-a").click();
        assertThat(page.locator("#frontlet-a")).isVisible();

        page.locator("#btn-switch-to-b").click();
        assertThat(page.locator("#frontlet-b")).isVisible();
    }

    @Test
    void loadFrontletViaLinkThenClickInIt() {
        navigateTo("/frontlets.html");

        page.locator("#load-frontlet-b").click();
        assertThat(page.locator("#frontlet-b")).isVisible();

        page.locator("#btn-click").click();
        page.locator("#btn-click").click();
        assertThat(page.locator("#clicks")).hasText("2");
    }
}
