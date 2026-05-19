package app

import one.xis.Action
import one.xis.FormData
import one.xis.ModelData
import one.xis.Page
import one.xis.WelcomePage

@WelcomePage
@Page('/groovy.html')
class GroovyHomePage {

    private final GroovyGreetingService greetingService
    private final AppLabel appLabel
    private String saved = 'nothing-saved'

    GroovyHomePage(GroovyGreetingService greetingService, AppLabel appLabel) {
        this.greetingService = greetingService
        this.appLabel = appLabel
    }

    @ModelData('headline')
    String headline() {
        'Groovy E2E'
    }

    @ModelData('injectedMessage')
    String injectedMessage() {
        greetingService.greeting()
    }

    @ModelData('beanLabel')
    String beanLabel() {
        appLabel.value
    }

    @ModelData
    List<String> getGroovyItems() {
        ['default-template', 'getter-modeldata', 'form-action']
    }

    @FormData('groovyForm')
    GroovyForm groovyForm() {
        new GroovyForm()
    }

    @ModelData('saved')
    String saved() {
        saved
    }

    @Action('save')
    void save(@FormData('groovyForm') GroovyForm form) {
        saved = "${form.name}:${form.amount}"
    }
}
