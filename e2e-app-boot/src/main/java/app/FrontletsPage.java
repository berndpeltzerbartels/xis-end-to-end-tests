package app;

import one.xis.ModelData;
import one.xis.Page;

/**
 * Tests: frontlet loading, frontlet switching in container.
 */
@Page("/frontlets.html")
public class FrontletsPage {

    @ModelData("title")
    String title() {
        return "Frontlet test";
    }
}
