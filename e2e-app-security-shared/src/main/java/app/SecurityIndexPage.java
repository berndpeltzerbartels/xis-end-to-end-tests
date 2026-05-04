package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.WelcomePage;

@WelcomePage
@Page("/index.html")
public class SecurityIndexPage {

    @ModelData("title")
    String title() {
        return "Security E2E Test App";
    }
}
