package one.xis.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ThemeE2ETest extends BootAppE2ETest {

    @Test
    void themeAssetsAreLoadedAutomaticallyAndCanBeCustomized() {
        navigateTo("/theme.html");
        assertThat(page.locator("#theme-button")).isVisible();

        @SuppressWarnings("unchecked")
        List<String> stylesheets = (List<String>) page.evaluate("""
            () => Array.from(document.querySelectorAll('link[rel="stylesheet"]'))
                .map(link => new URL(link.href).pathname)
            """);

        assertThat(stylesheets).contains("/default-theme.css", "/xis.css", "/theme.css");
        assertThat(stylesheets.indexOf("/default-theme.css")).isLessThan(stylesheets.indexOf("/xis.css"));
        assertThat(stylesheets.indexOf("/xis.css")).isLessThan(stylesheets.indexOf("/theme.css"));

        Object accent = page.evaluate("""
            () => getComputedStyle(document.documentElement).getPropertyValue('--accent').trim()
            """);
        Object buttonColor = page.evaluate("""
            () => getComputedStyle(document.querySelector('#theme-button')).backgroundColor
            """);

        assertThat(accent).isEqualTo("#2563eb");
        assertThat(buttonColor).isEqualTo("rgb(37, 99, 235)");

        Locator logo = page.locator("#theme-nav .logo img");
        assertThat(logo).isVisible();
        assertThat(logo).hasAttribute("src", Pattern.compile(".*/theme-logo\\.svg$"));
        assertThat((Boolean) page.evaluate("""
            () => {
                const logo = document.querySelector('#theme-nav .logo img');
                return logo.complete && logo.naturalWidth > 0;
            }
            """)).isTrue();
    }

    @Test
    void themeGridAndSpansCreateExpectedLayout() {
        navigateTo("/theme.html");
        assertThat(page.locator("#grid-notes")).isVisible();

        BoundingBox name = wrapperBox("#grid-name");
        BoundingBox city = wrapperBox("#grid-city");
        BoundingBox stage = wrapperBox("#grid-stage");
        BoundingBox email = wrapperBox("#grid-email");
        BoundingBox phone = wrapperBox("#grid-phone");
        BoundingBox notes = wrapperBox("#grid-notes");
        BoundingBox grid = box("#theme-grid");

        assertClose(city.y, name.y);
        assertThat(stage.y).isGreaterThan(name.y + name.height);
        assertThat(email.y).isGreaterThan(stage.y + stage.height);
        assertClose(phone.y, email.y);
        assertClose(notes.y, email.y);

        assertClose(name.x, stage.x);
        assertClose(stage.x, email.x);
        assertThat(name.width).isGreaterThan(city.width * 1.8);
        assertThat(city.x).isGreaterThan(name.x + name.width);

        assertThat(stage.width).isGreaterThan(name.width + city.width);

        assertThat(phone.x).isGreaterThan(email.x + email.width);
        assertThat(notes.x).isGreaterThan(phone.x + phone.width);
        assertClose(phone.width, email.width);
        assertClose(notes.width, email.width);

        Object gap = page.evaluate("""
            () => getComputedStyle(document.querySelector('#theme-grid')).columnGap
            """);
        assertThat(gap).isEqualTo("24px");

        Object columns = page.evaluate("""
            () => getComputedStyle(document.querySelector('#theme-grid')).gridTemplateColumns
            """);
        assertThat(columns.toString().trim().split("\\s+")).hasSize(3);
    }

    private BoundingBox box(String selector) {
        return page.locator(selector).boundingBox();
    }

    private BoundingBox wrapperBox(String selector) {
        return page.locator(selector).locator("xpath=ancestor::*[contains(concat(' ', normalize-space(@class), ' '), ' form-field ')][1]").boundingBox();
    }

    private void assertClose(double actual, double expected) {
        assertThat(actual).isCloseTo(expected, within(2.0));
    }
}
