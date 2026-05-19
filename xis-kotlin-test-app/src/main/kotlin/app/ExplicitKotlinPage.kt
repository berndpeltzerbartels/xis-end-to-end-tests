package app

import one.xis.HtmlFile
import one.xis.ModelData
import one.xis.Page

@Page("/explicit-kotlin.html")
@HtmlFile("ExplicitKotlinTemplate.html")
class ExplicitKotlinPage {
    @ModelData("explicitMessage")
    fun explicitMessage(): String = "explicit-htmlfile-ok"
}
