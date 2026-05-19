package app

import one.xis.boot.XISBootApplication
import one.xis.boot.XISBootRunner

@XISBootApplication
class Main {

    static void main(String[] args) {
        XISBootRunner.run(Main.class, args)
    }
}
