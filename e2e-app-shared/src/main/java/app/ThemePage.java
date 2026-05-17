package app;

import one.xis.Action;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.Page;

import java.util.List;

@Page("/theme.html")
public class ThemePage {

    @FormData("layout")
    LayoutForm layout() {
        var layout = new LayoutForm();
        layout.name = "Ada";
        layout.city = "London";
        layout.stage = "lead";
        layout.email = "ada@example.test";
        layout.phone = "+44 20";
        layout.notes = "First row";
        return layout;
    }

    @ModelData("stages")
    List<StageOption> stages() {
        return List.of(
                new StageOption("lead", "Lead"),
                new StageOption("customer", "Customer")
        );
    }

    @Action
    void saveLayout(@FormData("layout") LayoutForm layout) {
        // E2E page only checks rendering and binding.
    }

    public static class LayoutForm {
        public String name;
        public String city;
        public String stage;
        public String email;
        public String phone;
        public String notes;
    }

    public record StageOption(String code, String label) {
    }
}
