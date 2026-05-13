package app;

import one.xis.Frontlet;
import one.xis.ModelData;
import one.xis.Parameter;

import java.util.Map;

@Frontlet(url = "/distributed-next-frontlet", containerId = "remote-frontlet")
public class DistributedNextRemoteFrontlet {

    @ModelData("nextFrontletServer")
    String nextFrontletServer() {
        return DistributedShellPage.serverRole();
    }

    @ModelData("nextFrontletMessage")
    String nextFrontletMessage(@Parameter("message") String message) {
        return message;
    }

    @ModelData("nextFrontletParameters")
    String nextFrontletParameters(@Parameter Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .sorted()
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }
}
