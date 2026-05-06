package app;

import one.xis.context.Component;
import one.xis.distributed.XisDistributedConfig;

import java.util.Set;

@Component
public class DistributedRoutingConfig implements XisDistributedConfig {

    static final String REMOTE_PAGE = "/distributed-remote.html";
    static final String REMOTE_FRONTLET = "DistributedRemoteFrontlet";

    @Override
    public boolean isRemoteWidget(String widgetId) {
        return REMOTE_FRONTLET.equals(widgetId);
    }

    @Override
    public boolean isRemotePage(String normalizedPath) {
        return REMOTE_PAGE.equals(normalizedPath);
    }

    @Override
    public String getWidgetHost(String widgetId) {
        return isRemoteWidget(widgetId) ? remoteHost() : null;
    }

    @Override
    public String getPageHost(String normalizedPath) {
        return isRemotePage(normalizedPath) ? remoteHost() : null;
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
