package app

import one.xis.context.Component

@Component
class KotlinGreetingService {
    fun greeting(): String = "constructor-injection-ok"
}
