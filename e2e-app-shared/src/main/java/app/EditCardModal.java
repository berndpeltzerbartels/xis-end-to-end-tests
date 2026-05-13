package app;

import lombok.Data;
import one.xis.Action;
import one.xis.FormData;
import one.xis.Modal;
import one.xis.ModalResponse;
import one.xis.Parameter;
import one.xis.validation.Mandatory;

@Modal("/modals/card/{card}/edit")
public class EditCardModal {

    @Data
    public static class CardForm {
        @Mandatory
        private String value = "";
    }

    @FormData("cardForm")
    CardForm cardForm() {
        return new CardForm();
    }

    @Action
    ModalResponse save(@Parameter("card") String card, @FormData("cardForm") CardForm form) {
        ModalPage.saveCardValue(card, form.getValue());
        return ModalResponse.close().reloadParent();
    }
}
