package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;

@Page("/drag-drop.html")
public class DragDropPage {

    private String lastMove = "none";

    @ModelData("source")
    String source() {
        return "a2";
    }

    @ModelData("target")
    String target() {
        return "a4";
    }

    @ModelData("lastMove")
    String lastMove() {
        return lastMove;
    }

    @Action
    void move(String from, String to) {
        lastMove = from + "-" + to;
    }
}
