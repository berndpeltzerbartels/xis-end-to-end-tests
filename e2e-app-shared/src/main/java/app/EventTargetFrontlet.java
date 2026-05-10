package app;

import one.xis.Frontlet;
import one.xis.ModelData;
import one.xis.RefreshOnUpdateEvents;

@Frontlet
@RefreshOnUpdateEvents("source-frontlet-event")
public class EventTargetFrontlet {

    private final EventState eventState;
    private int renderCount;

    public EventTargetFrontlet() {
        this.eventState = EventState.get();
    }

    @ModelData("targetRenderCount")
    int targetRenderCount() {
        return ++renderCount;
    }

    @ModelData("sourceFrontletVersion")
    int sourceFrontletVersion() {
        return eventState.sourceFrontletVersion();
    }
}
