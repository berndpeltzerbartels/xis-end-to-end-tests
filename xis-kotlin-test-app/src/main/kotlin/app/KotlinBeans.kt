package app

import one.xis.context.Bean
import one.xis.context.Component

@Component
class KotlinBeans {
    @Bean
    fun appLabel(): AppLabel = AppLabel("bean-label-from-kotlin-bean")
}
