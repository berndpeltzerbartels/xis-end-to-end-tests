package app;

import one.xis.context.Component;
import one.xis.distributed.XisDistributedConfig;

import java.util.Map;
import java.util.Set;

@Component
public class DistributedSsoRoutingConfig implements XisDistributedConfig {

    static final String REMOTE_PAGE = "/distributed-sso-remote.html";

    @Override
    public Map<String, String> getPageHosts() {
        return Map.of(REMOTE_PAGE, remoteHost());
    }

    @Override
    public Set<String> getAllowedOrigins() {
        return Set.of(shellHost(), remoteHost());
    }

    private String shellHost() {
        var host = System.getProperty("e2e.distributed.sso.shellHost");
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("System property e2e.distributed.sso.shellHost is required");
        }
        return host;
    }

    private String remoteHost() {
        var host = System.getProperty("e2e.distributed.sso.remoteHost");
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("System property e2e.distributed.sso.remoteHost is required");
        }
        return host;
    }
}
