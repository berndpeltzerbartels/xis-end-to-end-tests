package app

import one.xis.boot.XISBootApplication
import one.xis.boot.XISBootRunner

@XISBootApplication
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            XISBootRunner.run(Main::class.java, args)
        }
    }
}
