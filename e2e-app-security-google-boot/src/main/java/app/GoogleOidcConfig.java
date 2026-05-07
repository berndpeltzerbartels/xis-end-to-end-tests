package app;

import one.xis.auth.idp.ExternalIDPConfig;
import one.xis.context.Component;

@Component
public class GoogleOidcConfig implements ExternalIDPConfig {

    @Override
    public String getIdpId() {
        return "google";
    }

    @Override
    public String getIdpServerUrl() {
        return "https://accounts.google.com";
    }

    @Override
    public String getClientId() {
        return requiredProperty("e2e.google.client.id");
    }

    @Override
    public String getClientSecret() {
        return requiredProperty("e2e.google.client.secret");
    }

    @Override
    public String getScope() {
        return "openid profile email";
    }

    private String requiredProperty(String name) {
        var value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("System property '" + name + "' not set.");
        }
        return value;
    }
}
