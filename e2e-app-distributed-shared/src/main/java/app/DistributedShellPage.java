package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.WelcomePage;

@WelcomePage
@Page("/distributed-shell.html")
public class DistributedShellPage {

    @ModelData("shellServer")
    String shellServer() {
        return serverRole();
    }

    static String serverRole() {
        return System.getProperty("e2e.distributed.role", "unknown");
    }
}
