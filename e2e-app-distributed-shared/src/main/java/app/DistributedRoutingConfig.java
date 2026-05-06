package app;

import one.xis.context.Component;
import one.xis.distributed.XisDistributedConfig;

import java.util.Map;
import java.util.Set;

@Component
public class DistributedRoutingConfig implements XisDistributedConfig {

    static final String REMOTE_PAGE = "/distributed-remote.html";
    static final String REMOTE_FRONTLET = "DistributedRemoteFrontlet";
    static final String NEXT_REMOTE_FRONTLET = "DistributedNextRemoteFrontlet";

    @Override
    public Map<String, String> getFrontletHosts() {
        return Map.of(
                REMOTE_FRONTLET, remoteHost(),
                NEXT_REMOTE_FRONTLET, remoteHost()
        );
    }

    @Override
    public Map<String, String> getPageHosts() {
        return Map.of(REMOTE_PAGE, remoteHost());
    }

    @Override
    public Set<String> getAllowedOrigins() {
        return Set.of(pageHost(), remoteHost());
    }

    private String pageHost() {
        var host = System.getProperty("e2e.distributed.pageHost");
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("System property e2e.distributed.pageHost is required");
        }
        return host;
    }

    private String remoteHost() {
        var host = System.getProperty("e2e.distributed.remoteHost");
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("System property e2e.distributed.remoteHost is required");
        }
        return host;
    }
}
