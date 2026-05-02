package app;

import lombok.Data;
import one.xis.Action;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.Page;

/**
 * Tests: form binding, validation feedback, submit + reset.
 */
@Page("/form.html")
public class FormPage {

    @Data
    public static class PersonForm {
        private String name = "";
        private String email = "";
    }

    @FormData("person")
    PersonForm person() {
        return new PersonForm();
    }

    private String savedName;
    private String savedEmail;

    @ModelData("savedName")
    String savedName() {
        return savedName != null ? savedName : "";
    }

    @ModelData("savedEmail")
    String savedEmail() {
        return savedEmail != null ? savedEmail : "";
    }

    @ModelData("submitted")
    boolean submitted() {
        return savedName != null;
    }

    @Action
    void submit(@FormData("person") PersonForm person) {
        savedName  = person.getName();
        savedEmail = person.getEmail();
    }

    @Action
    void reset() {
        savedName = null;
        savedEmail = null;
    }
}
