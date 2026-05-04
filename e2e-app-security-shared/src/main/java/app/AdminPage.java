package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.Roles;

@Page("/admin.html")
@Roles("ADMIN")
public class AdminPage {

    @ModelData("message")
    String message() {
        return "Admin content";
    }
}
