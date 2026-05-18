package app.boothttpnative;

import one.xis.http.Controller;
import one.xis.http.Get;
import one.xis.http.UrlParameter;

@Controller
class BootHttpNativeProbeController {

    @Get("/api/probe")
    String probe(@UrlParameter("name") String name) {
        return "boot-http-native:" + name;
    }
}
