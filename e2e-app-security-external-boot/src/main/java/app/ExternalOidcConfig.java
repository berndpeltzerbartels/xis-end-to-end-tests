package app;

import one.xis.auth.idp.ExternalIDPConfig;
import one.xis.context.Component;

@Component
public class ExternalOidcConfig implements ExternalIDPConfig {

    @Override
    public String getIdpId() {
        return "mock-oidc";
    }

    @Override
    public String getIdpServerUrl() {
        return System.getProperty("e2e.oidc.url");
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
