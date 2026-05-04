package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.WelcomePage;

import java.util.List;

/**
 * Welcome page – lists all feature pages.
 * Tests: basic rendering, model data, page navigation.
 */
@WelcomePage
@Page("/index.html")
public class IndexPage {

    @ModelData("title")
    String title() {
        return "XIS E2E Test App";
    }

    @ModelData("features")
    List<String> features() {
        return List.of("navigation", "frontlets", "form", "counter", "expression-language");
    }
}
