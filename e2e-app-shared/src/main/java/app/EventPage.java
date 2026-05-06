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
    private int version;

    public EventPage(RefreshEventPublisher refreshEventPublisher) {
        this.refreshEventPublisher = refreshEventPublisher;
    }

    @ModelData("version")
    int version() {
        return version;
    }

    @Action
    void publishToAll() {
        version++;
        refreshEventPublisher.publishToAll("core-event");
    }

    @Action
    void publishToCurrentClient(@ClientId String clientId) {
        version++;
        refreshEventPublisher.publishToClient("core-event", clientId);
    }

    @Action
    void resetEvents() {
        version = 0;
        refreshEventPublisher.publishToAll("core-event");
    }
}
