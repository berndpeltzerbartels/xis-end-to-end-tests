package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.FrontletResponse;
import one.xis.ModelData;

@Frontlet
public class FrontletA {

    private int clicks = 0;

    @ModelData("label")
    String label() {
        return "Frontlet A";
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
    FrontletResponse switchToB() {
        return new FrontletResponse(FrontletB.class);
    }
}
