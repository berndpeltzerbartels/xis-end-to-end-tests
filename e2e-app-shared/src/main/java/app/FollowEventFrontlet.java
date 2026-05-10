package app;

import one.xis.Frontlet;
import one.xis.ModelData;
import one.xis.RefreshOnUpdateEvents;

@Frontlet
@RefreshOnUpdateEvents("follow-frontlet-event")
public class FollowEventFrontlet {

    private final EventState eventState;

    public FollowEventFrontlet() {
        this.eventState = EventState.get();
    }

    @ModelData("followFrontletVersion")
    int followFrontletVersion() {
        return eventState.followFrontletVersion();
    }
}
