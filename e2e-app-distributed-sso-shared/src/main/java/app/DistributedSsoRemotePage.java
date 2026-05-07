package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;
import one.xis.RefreshEventPublisher;
import one.xis.RefreshOnUpdateEvents;
import one.xis.Roles;
import one.xis.UserId;

import java.util.concurrent.atomic.AtomicInteger;

@Page("/distributed-sso-remote.html")
@Roles("USER")
@RefreshOnUpdateEvents("distributed-sso-user-event")
public class DistributedSsoRemotePage {

    private static final AtomicInteger VERSION = new AtomicInteger();

    private final RefreshEventPublisher refreshEventPublisher;

    public DistributedSsoRemotePage(RefreshEventPublisher refreshEventPublisher) {
        this.refreshEventPublisher = refreshEventPublisher;
    }

    @ModelData("userId")
    String userId(@UserId String userId) {
        return userId;
    }

    @ModelData("version")
    int version() {
        return VERSION.get();
    }

    @Action
    void publishToCurrentUser(@UserId String userId) {
        VERSION.incrementAndGet();
        refreshEventPublisher.publishToUser("distributed-sso-user-event", userId);
    }
}
