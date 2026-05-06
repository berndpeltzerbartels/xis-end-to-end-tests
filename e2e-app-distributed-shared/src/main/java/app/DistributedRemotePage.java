package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;

@Page("/distributed-remote.html")
public class DistributedRemotePage {

    private int count;

    @ModelData("remotePageServer")
    String remotePageServer() {
        return DistributedShellPage.serverRole();
    }

    @ModelData("remotePageCount")
    int remotePageCount() {
        return count;
    }

    @Action
    void incrementRemotePage() {
        count++;
    }
}
