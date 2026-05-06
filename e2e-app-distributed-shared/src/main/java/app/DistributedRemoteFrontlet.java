package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.ModelData;

@Frontlet
public class DistributedRemoteFrontlet {

    private int count;

    @ModelData("frontletServer")
    String frontletServer() {
        return DistributedShellPage.serverRole();
    }

    @ModelData("frontletCount")
    int frontletCount() {
        return count;
    }

    @Action
    void incrementFrontlet() {
        count++;
    }
}
