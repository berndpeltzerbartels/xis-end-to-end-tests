package app;

public class EventState {

    private static final EventState INSTANCE = new EventState();

    private int sourcePageVersion;
    private int followPageVersion;
    private int sourceFrontletVersion;
    private int followFrontletVersion;

    static EventState get() {
        return INSTANCE;
    }

    int sourcePageVersion() {
        return sourcePageVersion;
    }

    int followPageVersion() {
        return followPageVersion;
    }

    int sourceFrontletVersion() {
        return sourceFrontletVersion;
    }

    int followFrontletVersion() {
        return followFrontletVersion;
    }

    void incrementSourcePageVersion() {
        sourcePageVersion++;
    }

    void incrementFollowPageVersion() {
        followPageVersion++;
    }

    void incrementSourceFrontletVersion() {
        sourceFrontletVersion++;
    }

    void incrementFollowFrontletVersion() {
        followFrontletVersion++;
    }

    void reset() {
        sourcePageVersion = 0;
        followPageVersion = 0;
        sourceFrontletVersion = 0;
        followFrontletVersion = 0;
    }
}
