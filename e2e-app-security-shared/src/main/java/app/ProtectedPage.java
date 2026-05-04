package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.Roles;
import one.xis.UserId;

@Page("/protected.html")
@Roles("USER")
public class ProtectedPage {

    @ModelData("message")
    String message(@UserId String userId) {
        return "Protected content for " + userId;
    }
}
