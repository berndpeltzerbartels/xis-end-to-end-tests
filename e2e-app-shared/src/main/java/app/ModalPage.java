package app;

import one.xis.Action;
import one.xis.Frontlet;
import one.xis.ModelData;
import one.xis.ModalResponse;
import one.xis.Page;
import one.xis.Parameter;

import java.util.HashMap;
import java.util.Map;

@Page("/modals.html")
public class ModalPage {

    static String pageValue = "initial";
    static final Map<String, Integer> cardVersions = new HashMap<>();
    static final Map<String, String> cardValues = new HashMap<>();

    @ModelData("pageValue")
    String pageValue() {
        return pageValue;
    }

    @Action
    ModalResponse openFromAction() {
        return ModalResponse.open(EditValueModal.class).parameter("source", "page-action");
    }

    static void savePageValue(String value) {
        pageValue = value;
    }

    static void saveCardValue(String card, String value) {
        cardValues.put(card, value);
        cardVersions.merge(card, 1, Integer::sum);
    }

    @Frontlet("ModalCard")
    public static class ModalCard {

        @ModelData("card")
        String card(@Parameter("card") String card) {
            return card;
        }

        @ModelData("cardValue")
        String cardValue(@Parameter("card") String card) {
            return cardValues.getOrDefault(card, "empty");
        }

        @ModelData("cardVersion")
        int cardVersion(@Parameter("card") String card) {
            return cardVersions.getOrDefault(card, 0);
        }
    }
}
