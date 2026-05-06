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
    void gridClassesCreateThreeAlignedColumns() {
        navigateTo("/theme.html");
        assertThat(page.locator("#grid-f")).isVisible();

        BoundingBox a = box("#grid-a");
        BoundingBox b = box("#grid-b");
        BoundingBox c = box("#grid-c");
        BoundingBox d = box("#grid-d");
        BoundingBox e = box("#grid-e");
        BoundingBox f = box("#grid-f");

        assertClose(b.y, a.y);
        assertClose(c.y, a.y);
        assertClose(e.y, d.y);
        assertClose(f.y, d.y);
        assertThat(d.y).isGreaterThan(a.y + a.height);

        assertClose(d.x, a.x);
        assertClose(e.x, b.x);
        assertClose(f.x, c.x);
        assertThat(b.x).isGreaterThan(a.x + a.width);
        assertThat(c.x).isGreaterThan(b.x + b.width);

        Object gap = page.evaluate("""
            () => getComputedStyle(document.querySelector('#theme-grid')).columnGap
            """);
        assertThat(gap).isEqualTo("24px");
    }

    private BoundingBox box(String selector) {
        return page.locator(selector).boundingBox();
    }

    private void assertClose(double actual, double expected) {
        assertThat(actual).isCloseTo(expected, within(2.0));
    }
}
