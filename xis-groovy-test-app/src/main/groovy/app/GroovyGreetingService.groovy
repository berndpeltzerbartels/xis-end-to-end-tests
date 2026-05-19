package app

import one.xis.context.Component

@Component
class GroovyGreetingService {

    String greeting() {
        'constructor-injection-ok'
    }
}
