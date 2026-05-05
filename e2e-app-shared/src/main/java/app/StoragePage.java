package app;

import one.xis.Action;
import one.xis.LocalStorage;
import one.xis.Page;
import one.xis.SessionStorage;

@Page("/storage.html")
public class StoragePage {

    @Action
    void incrementLocal(@LocalStorage("localCounter") CounterState localCounter) {
        localCounter.increment();
    }

    @Action
    void incrementSession(@SessionStorage("sessionCounter") CounterState sessionCounter) {
        sessionCounter.increment();
    }

    @Action
    void resetLocal(@LocalStorage("localCounter") CounterState localCounter) {
        localCounter.reset();
    }

    @Action
    void resetSession(@SessionStorage("sessionCounter") CounterState sessionCounter) {
        sessionCounter.reset();
    }

    public static class CounterState {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        void increment() {
            count++;
        }

        void reset() {
            count = 0;
        }
    }
}
