package app

import one.xis.HtmlFile
import one.xis.ModelData
import one.xis.Page

@Page('/explicit-groovy.html')
@HtmlFile('ExplicitGroovyTemplate.html')
class ExplicitGroovyPage {

    @ModelData('explicitMessage')
    String explicitMessage() {
        'explicit-htmlfile-ok'
    }
}
