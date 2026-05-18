package app.boothttp;

import one.xis.http.Controller;
import one.xis.http.Get;
import one.xis.http.UrlParameter;

@Controller
class BootHttpProbeController {

    @Get("/api/probe")
    String probe(@UrlParameter("name") String name) {
        return "boot-http:" + name;
    }
}
