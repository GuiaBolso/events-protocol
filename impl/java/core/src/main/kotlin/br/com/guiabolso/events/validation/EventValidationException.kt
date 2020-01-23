package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.EventMessage

class EventValidationException(propertyName: String) : RuntimeException() {

    val eventMessage = EventMessage(
        "INVALID_COMMUNICATION_PROTOCOL",
        mapOf("missingProperty" to propertyName)
    )

}