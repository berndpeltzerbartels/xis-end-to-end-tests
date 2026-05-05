package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;
import one.xis.RefreshOnUpdateEvents;
import one.xis.Roles;
import one.xis.UserId;
import one.xis.server.RefreshEventPublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Page("/user-events.html")
@Roles("USER")
@RefreshOnUpdateEvents("user-event")
public class UserEventPage {

    private final RefreshEventPublisher refreshEventPublisher;
    private final Map<String, Integer> versionsByUser = new ConcurrentHashMap<>();

    public UserEventPage(RefreshEventPublisher refreshEventPublisher) {
        this.refreshEventPublisher = refreshEventPublisher;
    }

    @ModelData("userId")
    String userId(@UserId String userId) {
        return userId;
    }

    @ModelData("version")
    int version(@UserId String userId) {
        return versionsByUser.getOrDefault(userId, 0);
    }

    @Action
    void publishToCurrentUser(@UserId String userId) {
        versionsByUser.merge(userId, 1, Integer::sum);
        refreshEventPublisher.publishToUser("user-event", userId);
    }

    @Action
    void resetUserEvent(@UserId String userId) {
        versionsByUser.put(userId, 0);
        refreshEventPublisher.publishToUser("user-event", userId);
    }
}
