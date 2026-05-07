package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.Roles;
import one.xis.UserId;

@Page("/mapped.html")
@Roles("MAPPED_USER")
public class ExternalMappedPage {

    @ModelData("message")
    String message(@UserId String userId) {
        return "Mapped external account for " + userId;
    }
}
