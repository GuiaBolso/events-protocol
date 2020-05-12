package br.com.guiabolso.events.server.parser

import br.com.guiabolso.events.model.EventMessage

class EventParsingException(cause: Throwable) : RuntimeException(cause.message, cause) {
    val eventMessage: EventMessage = EventMessage(
        "INVALID_COMMUNICATION_PROTOCOL",
        mapOf("message" to cause.message)
    )
}
