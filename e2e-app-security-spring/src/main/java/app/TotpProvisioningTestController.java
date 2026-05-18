package app;

import one.xis.context.AppContext;
import one.xis.http.ContentType;
import one.xis.http.Controller;
import one.xis.http.Get;
import one.xis.http.Produces;
import one.xis.http.ResponseEntity;
import one.xis.http.UrlParameter;

import java.lang.reflect.Method;

@Controller("/e2e/totp")
public class TotpProvisioningTestController {

    private final AppContext appContext;

    public TotpProvisioningTestController(AppContext appContext) {
        this.appContext = appContext;
    }

    @Get("/provisioning-uri")
    @Produces(ContentType.TEXT_PLAIN)
    ResponseEntity<String> provisioningUri(@UrlParameter("userId") String userId) {
        return ResponseEntity.ok(provisioningUriFor(userId))
                .addHeader("Cache-Control", "no-store");
    }

    private String provisioningUriFor(String userId) {
        try {
            Class<?> type = Class.forName("one.xis.totp.TOTPProvisioningService");
            Object service = appContext.getSingleton(type);
            Method method = type.getDeclaredMethod("provisioningUri", String.class);
            method.setAccessible(true);
            return (String) method.invoke(service, userId);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to get TOTP provisioning URI", e);
        }
    }
}
