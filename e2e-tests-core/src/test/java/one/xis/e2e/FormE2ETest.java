package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests form binding, submit and reset.
 */
class FormE2ETest extends BootAppE2ETest {

    @Test
    void formIsRendered() {
        openForm();
        assertThat(page.locator("#input-name")).isVisible();
        assertThat(page.locator("#input-email")).isVisible();
        assertThat(page.locator("#result")).not().isVisible();
    }

    @Test
    void submitShowsResult() {
        openForm();
        page.locator("#input-name").fill("Alice");
        page.locator("#input-email").fill("alice@example.com");
        page.locator("#btn-submit").click();

        assertThat(page.locator("#saved-name")).hasText("Alice");
        assertThat(page.locator("#saved-email")).hasText("alice@example.com");
    }

    @Test
    void resetClearsResult() {
        openForm();
        page.locator("#input-name").fill("Alice");
        page.locator("#input-email").fill("alice@example.com");
        page.locator("#btn-submit").click();
        assertThat(page.locator("#result")).isVisible();

        page.locator("#btn-reset").click();
        assertThat(page.locator("#result")).not().isVisible();
    }

    @Test
    void submitMultipleTimes() {
        openForm();

        page.locator("#input-name").fill("Alice");
        page.locator("#input-email").fill("alice@example.com");
        page.locator("#btn-submit").click();
        assertThat(page.locator("#saved-name")).hasText("Alice");

        // Second submit with different data
        page.locator("#input-name").fill("Bob");
        page.locator("#input-email").fill("bob@example.com");
        page.locator("#btn-submit").click();
        assertThat(page.locator("#saved-name")).hasText("Bob");
    }

    private void openForm() {
        navigateTo("/form.html");
        page.locator("#btn-reset").click();
        assertThat(page.locator("#result")).not().isVisible();
    }
}
