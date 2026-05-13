package app;

import lombok.Data;
import one.xis.Action;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.validation.Mandatory;

@Modal("/modals/edit")
public class EditValueModal {

    @Data
    public static class ValueForm {
        @Mandatory
        private String value = "";
    }

    @FormData("valueForm")
    ValueForm valueForm() {
        return new ValueForm();
    }

    @Action
    ModalResponse save(@FormData("valueForm") ValueForm form) {
        ModalPage.savePageValue(form.getValue());
        return ModalResponse.close().reloadParent();
    }

    @Action
    ModalResponse cancel() {
        return ModalResponse.close();
    }

    @one.xis.ModelData("source")
    String source(@Parameter("source") String source) {
        return source;
    }
}
