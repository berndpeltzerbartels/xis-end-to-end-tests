package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests action-based state with multiple sequential clicks.
 * This is the key regression test for the "only first click works" bug.
 */
class CounterE2ETest extends BootAppE2ETest {

    @Test
    void initialCountIsZero() {
        openCounter();
        assertThat(page.locator("#count")).hasText("0");
    }

    @Test
    void incrementOnce() {
        openCounter();
        page.locator("#btn-increment").click();
        assertThat(page.locator("#count")).hasText("1");
    }

    @Test
    void incrementMultipleTimes() {
        openCounter();
        page.locator("#btn-increment").click();
        page.locator("#btn-increment").click();
        page.locator("#btn-increment").click();
        assertThat(page.locator("#count")).hasText("3");
    }

    @Test
    void incrementThenDecrement() {
        openCounter();
        page.locator("#btn-increment").click();
        page.locator("#btn-increment").click();
        page.locator("#btn-decrement").click();
        assertThat(page.locator("#count")).hasText("1");
    }

    @Test
    void reset() {
        openCounter();
        page.locator("#btn-increment").click();
        page.locator("#btn-increment").click();
        page.locator("#btn-increment").click();
        page.locator("#btn-reset").click();
        assertThat(page.locator("#count")).hasText("0");
    }

    @Test
    void manyClicksInSequence() {
        openCounter();
        for (int i = 0; i < 10; i++) {
            page.locator("#btn-increment").click();
        }
        assertThat(page.locator("#count")).hasText("10");
    }

    private void openCounter() {
        navigateTo("/counter.html");
        page.locator("#btn-reset").click();
        assertThat(page.locator("#count")).hasText("0");
    }
}
