package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.RefreshOnUpdateEvents;

@Page("/follow-events.html")
@RefreshOnUpdateEvents("follow-page-event")
public class FollowEventPage {

    private final EventState eventState;

    public FollowEventPage() {
        this.eventState = EventState.get();
    }

    @ModelData("followVersion")
    int followVersion() {
        return eventState.followPageVersion();
    }
}
