package one.xis.e2e;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class StorageE2ETest extends BootAppE2ETest {

    @Test
    void localStorageParameterIsReadUpdatedAndRendered() {
        openStorage();

        page.locator("#local-increment").click();
        assertThat(page.locator("#local-count")).hasText("1");
        org.assertj.core.api.Assertions.assertThat(localStorageItem("localCounter")).isEqualTo("{\"value\":{\"count\":1}}");

        page.locator("#local-increment").click();
        assertThat(page.locator("#local-count")).hasText("2");
        org.assertj.core.api.Assertions.assertThat(localStorageItem("localCounter")).isEqualTo("{\"value\":{\"count\":2}}");

        page.reload();
        assertThat(page.locator("#local-count")).hasText("2");
    }

    @Test
    void sessionStorageParameterIsReadUpdatedAndRendered() {
        openStorage();

        page.locator("#session-increment").click();
        assertThat(page.locator("#session-count")).hasText("1");
        org.assertj.core.api.Assertions.assertThat(sessionStorageItem("sessionCounter")).isEqualTo("{\"value\":{\"count\":1}}");

        page.locator("#session-increment").click();
        assertThat(page.locator("#session-count")).hasText("2");
        org.assertj.core.api.Assertions.assertThat(sessionStorageItem("sessionCounter")).isEqualTo("{\"value\":{\"count\":2}}");

        page.reload();
        assertThat(page.locator("#session-count")).hasText("2");
    }

    @Test
    void resettingStorageMutatesBrowserValueAndRefreshesBinding() {
        openStorage();

        page.locator("#local-increment").click();
        page.locator("#session-increment").click();
        assertThat(page.locator("#local-count")).hasText("1");
        assertThat(page.locator("#session-count")).hasText("1");

        page.locator("#local-reset").click();
        page.locator("#session-reset").click();

        assertThat(page.locator("#local-count")).hasText("0");
        assertThat(page.locator("#session-count")).hasText("0");
        org.assertj.core.api.Assertions.assertThat(localStorageItem("localCounter")).isEqualTo("{\"value\":{\"count\":0}}");
        org.assertj.core.api.Assertions.assertThat(sessionStorageItem("sessionCounter")).isEqualTo("{\"value\":{\"count\":0}}");
    }

    private void openStorage() {
        navigateTo("/storage.html");
        page.evaluate("localStorage.removeItem('localCounter')");
        page.evaluate("sessionStorage.removeItem('sessionCounter')");
        page.reload();
        assertThat(page.locator("#local-count")).hasText("0");
        assertThat(page.locator("#session-count")).hasText("0");
    }

    private String localStorageItem(String key) {
        return (String) page.evaluate("key => localStorage.getItem(key)", key);
    }

    private String sessionStorageItem(String key) {
        return (String) page.evaluate("key => sessionStorage.getItem(key)", key);
    }
}
