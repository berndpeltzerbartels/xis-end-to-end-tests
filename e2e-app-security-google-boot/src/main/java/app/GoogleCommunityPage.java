package app;

import one.xis.ModelData;
import one.xis.Page;
import one.xis.Roles;
import one.xis.UserId;

@Page("/community.html")
@Roles
public class GoogleCommunityPage {

    @ModelData("message")
    String message(@UserId String userId) {
        return "Community content for " + userId;
    }
}
