package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.FrontletResponse;
import one.xis.ModelData;

@Frontlet(url = "/distributed-remote-frontlet", containerId = "remote-frontlet")
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

    @Action
    FrontletResponse openNextFrontletByUrl() {
        return new FrontletResponse("/distributed-next-frontlet?message=from-url&source=remote-frontlet");
    }
}
