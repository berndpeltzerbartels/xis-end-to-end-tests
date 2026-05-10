package app;

import one.xis.Action;
import one.xis.ClientId;
import one.xis.ModelData;
import one.xis.Page;
import one.xis.RefreshOnUpdateEvents;
import one.xis.RefreshEventPublisher;

@Page("/events.html")
@RefreshOnUpdateEvents("core-event")
public class EventPage {

    private final RefreshEventPublisher refreshEventPublisher;
    private final EventState eventState;

    public EventPage(RefreshEventPublisher refreshEventPublisher) {
        this.refreshEventPublisher = refreshEventPublisher;
        this.eventState = EventState.get();
    }

    @ModelData("version")
    int version() {
        return eventState.sourcePageVersion();
    }

    @Action
    void publishToAll() {
        eventState.incrementSourcePageVersion();
        refreshEventPublisher.publishToAll("core-event");
    }

    @Action
    void publishToCurrentClient(@ClientId String clientId) {
        eventState.incrementSourcePageVersion();
        refreshEventPublisher.publishToClient("core-event", clientId);
    }

    @Action
    void resetEvents() {
        eventState.reset();
        refreshEventPublisher.publishToAll("core-event");
    }

    @Action
    void publishFollowPageEvent() {
        eventState.incrementFollowPageVersion();
        refreshEventPublisher.publishToAll("follow-page-event");
    }

    @Action
    void publishSourceFrontletEvent() {
        eventState.incrementSourceFrontletVersion();
        refreshEventPublisher.publishToAll("source-frontlet-event");
    }

    @Action
    void publishFollowFrontletEvent() {
        eventState.incrementFollowFrontletVersion();
        refreshEventPublisher.publishToAll("follow-frontlet-event");
    }
}
