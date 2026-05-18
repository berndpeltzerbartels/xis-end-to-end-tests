package app;

import one.xis.Action;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.Page;
import one.xis.Roles;

@Page("/editor.html")
@Roles("USER")
public class EditorPage {

    private String savedValue = "not saved";

    @FormData("editor")
    EditorForm form() {
        return new EditorForm("article-editor", "draft");
    }

    @ModelData("savedValue")
    String savedValue() {
        return savedValue;
    }

    @Action
    void save(@FormData("editor") EditorForm form) {
        savedValue = form.value();
    }
}
