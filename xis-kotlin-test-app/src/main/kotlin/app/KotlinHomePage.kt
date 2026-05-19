package app

import one.xis.Action
import one.xis.FormData
import one.xis.ModelData
import one.xis.Page
import one.xis.WelcomePage

@WelcomePage
@Page("/kotlin.html")
class KotlinHomePage(
    private val greetingService: KotlinGreetingService,
    private val appLabel: AppLabel
) {
    private var saved: String = "nothing-saved"

    @ModelData("headline")
    fun headline(): String = "Kotlin E2E"

    @ModelData("injectedMessage")
    fun injectedMessage(): String = greetingService.greeting()

    @ModelData("beanLabel")
    fun beanLabel(): String = appLabel.value

    @ModelData
    fun getKotlinItems(): List<String> = listOf("default-template", "getter-modeldata", "form-action")

    @FormData("kotlinForm")
    fun kotlinForm(): KotlinForm = KotlinForm()

    @ModelData("saved")
    fun saved(): String = saved

    @Action("save")
    fun save(@FormData("kotlinForm") form: KotlinForm) {
        saved = "${form.name}:${form.amount}"
    }
}
