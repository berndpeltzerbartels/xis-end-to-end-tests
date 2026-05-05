package app;

import one.xis.auth.idp.ExternalIDPConfig;
import one.xis.context.Component;

@Component
public class XisIdpConfig implements ExternalIDPConfig {

    @Override
    public String getIdpId() {
        return "xis-idp";
    }

    @Override
    public String getIdpServerUrl() {
        return System.getProperty("e2e.xis.idp.url");
    }

    @Override
    public String getClientId() {
        return "xis-e2e-client";
    }

    @Override
    public String getClientSecret() {
        return "xis-e2e-secret";
    }
}
