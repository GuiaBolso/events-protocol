package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.EventMessage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EventValidationException(propertyName: String) : RuntimeException() {

    val eventMessage = EventMessage(
        "INVALID_COMMUNICATION_PROTOCOL",
        buildJsonObject {
            put("missingProperty", propertyName)
        }
    )
}
