package app;

import one.xis.Frontlet;
import one.xis.ModelData;

@Frontlet
public class EventTargetFrontlet {

    private int renderCount;

    @ModelData("targetRenderCount")
    int targetRenderCount() {
        return ++renderCount;
    }
}
