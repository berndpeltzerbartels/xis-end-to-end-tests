package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;

/**
 * Tests: multi-step navigation, back-navigation, path-variables.
 */
@Page("/navigation.html")
public class NavigationPage {

    @ModelData("step")
    String step() {
        return "navigation-page";
    }

    @Action
    Class<?> goToForm() {
        return FormPage.class;
    }

    @Action
    Class<?> goToCounter() {
        return CounterPage.class;
    }

    @Action
    Class<?> goHome() {
        return IndexPage.class;
    }
}
