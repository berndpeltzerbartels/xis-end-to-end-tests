package app

import one.xis.validation.Mandatory

class KotlinForm {
    @Mandatory
    var name: String = ""
    var amount: Int = 1
}
