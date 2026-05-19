package app

import one.xis.context.Bean
import one.xis.context.Component

@Component
class GroovyBeans {

    @Bean
    AppLabel appLabel() {
        new AppLabel('bean-label-from-groovy-bean')
    }
}
