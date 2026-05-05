package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.FrontletResponse;

@Frontlet
public class EventSourceFrontlet {

    @Action
    FrontletResponse reloadTarget() {
        return new FrontletResponse().reloadFrontlet("EventTargetFrontlet");
    }
}
