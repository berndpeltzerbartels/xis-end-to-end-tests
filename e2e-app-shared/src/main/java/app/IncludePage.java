package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;

@Page("/includes.html")
public class IncludePage {

    private int includeClicks;

    @ModelData("includeTitle")
    String includeTitle() {
        return "Reusable navigation";
    }

    @ModelData("includeCount")
    int includeCount() {
        return includeClicks;
    }

    @Action
    void incrementFromInclude() {
        includeClicks++;
    }

    @Action
    void resetIncludeCount() {
        includeClicks = 0;
    }
}
