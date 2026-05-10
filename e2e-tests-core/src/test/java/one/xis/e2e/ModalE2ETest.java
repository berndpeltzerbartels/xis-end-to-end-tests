package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class ModalE2ETest extends BootAppE2ETest {

    @Test
    void modalCanBeOpenedValidatedSavedAndClosedFromPage() {
        navigateTo("/modals.html");

        page.locator("#open-page-modal").click();
        assertThat(page.locator("#edit-value-modal")).isVisible();
        assertThat(page.locator("#modal-source")).hasText("page-html");

        page.locator("#modal-save").click();
        assertThat(page.locator("#edit-value-modal")).isVisible();
        assertThat(page.locator("#modal-value")).hasClass("error");

        page.locator("#modal-value").fill("saved in modal");
        page.locator("#modal-save").click();

        assertThat(page.locator("#edit-value-modal")).not().isVisible();
        assertThat(page.locator("#page-value")).hasText("saved in modal");
    }

    @Test
    void modalCanBeOpenedFromActionResponse() {
        navigateTo("/modals.html");

        page.locator("#open-page-modal-action").click();

        assertThat(page.locator("#edit-value-modal")).isVisible();
        assertThat(page.locator("#modal-source")).hasText("page-action");
    }

    @Test
    void modalCanBeOpenedFromLinkWithoutFollowingHref() {
        navigateTo("/modals.html");

        page.locator("#open-page-modal-link").click();

        assertThat(page.locator("#edit-value-modal")).isVisible();
        assertThat(page.locator("#modal-source")).hasText("page-link");
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/modals\\.html$"));
    }

    @Test
    void modalReloadsOnlyTheOpeningFrontletInstance() {
        navigateTo("/modals.html");
        assertThat(page.locator("#card-one-version")).hasText("0");
        assertThat(page.locator("#card-two-version")).hasText("0");

        page.locator("#open-card-one-modal").click();
        assertThat(page.locator("#edit-card-modal")).isVisible();
        page.locator("#card-modal-value").fill("first card");
        page.locator("#card-modal-save").click();

        assertThat(page.locator("#edit-card-modal")).not().isVisible();
        assertThat(page.locator("#card-one-value")).hasText("first card");
        assertThat(page.locator("#card-one-version")).hasText("1");
        assertThat(page.locator("#card-two-value")).hasText("empty");
        assertThat(page.locator("#card-two-version")).hasText("0");
    }
}
