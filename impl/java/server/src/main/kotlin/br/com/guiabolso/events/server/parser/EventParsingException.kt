package br.com.guiabolso.events.server.parser

import br.com.guiabolso.events.model.EventMessage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EventParsingException(cause: Throwable) : RuntimeException(cause.message, cause) {
    val eventMessage: EventMessage = EventMessage(
        "INVALID_COMMUNICATION_PROTOCOL",
        buildJsonObject { put("message", cause.message) }
    )
}
