package one.xis.e2e;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class DragDropE2ETest extends BootAppE2ETest {

    @Test
    void dropCallsActionWithDragValueAndDropTargetValue() {
        navigateTo("/drag-drop.html");
        assertThat(page.locator("#source")).isVisible();
        assertThat(page.locator("#target")).isVisible();

        dispatchDragAndDrop("#source", "#target");

        assertThat(page.locator("#last-move")).hasText("a2-a4");
    }

    private void dispatchDragAndDrop(String sourceSelector, String targetSelector) {
        page.evaluate("""
                ([sourceSelector, targetSelector]) => {
                    const source = document.querySelector(sourceSelector);
                    const target = document.querySelector(targetSelector);
                    const dataTransfer = new DataTransfer();
                    source.dispatchEvent(new DragEvent('dragstart', {
                        bubbles: true,
                        cancelable: true,
                        dataTransfer
                    }));
                    target.dispatchEvent(new DragEvent('dragover', {
                        bubbles: true,
                        cancelable: true,
                        dataTransfer
                    }));
                    target.dispatchEvent(new DragEvent('drop', {
                        bubbles: true,
                        cancelable: true,
                        dataTransfer
                    }));
                }
                """, List.of(sourceSelector, targetSelector));
    }
}
