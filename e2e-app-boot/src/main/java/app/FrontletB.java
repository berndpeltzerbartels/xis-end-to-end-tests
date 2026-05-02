package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.FrontletResponse;
import one.xis.ModelData;

@Frontlet
public class FrontletB {

    private int clicks = 0;

    @ModelData("label")
    String label() {
        return "Frontlet B";
    }

    @ModelData("clicks")
    int clicks() {
        return clicks;
    }

    @Action
    void click() {
        clicks++;
    }

    @Action
    FrontletResponse switchToA() {
        return new FrontletResponse(FrontletA.class);
    }
}
