package app;

import one.xis.Action;
import one.xis.ModelData;
import one.xis.Page;

/**
 * Tests: action with state, multiple clicks, counter increments correctly.
 */
@Page("/counter.html")
public class CounterPage {

    private int count = 0;

    @ModelData("count")
    int count() {
        return count;
    }

    @Action
    void increment() {
        count++;
    }

    @Action
    void decrement() {
        count--;
    }

    @Action
    void reset() {
        count = 0;
    }
}
