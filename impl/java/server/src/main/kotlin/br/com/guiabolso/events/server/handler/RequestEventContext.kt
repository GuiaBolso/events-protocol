package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.builder.EventTemplate
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.model.AbstractEventContext
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

data class RequestEventContext(
    override val event: RequestEvent,
    override val jsonAdapter: JsonAdapter,
) : AbstractEventContext<RequestEvent>() {
    private val eventBuilder = EventBuilder(jsonAdapter)

    suspend fun response(
        operations: suspend EventTemplate.() -> Unit,
    ): ResponseEvent {
        return eventBuilder.responseFor(event, operations)
    }

    fun redirect(payload: RedirectPayload): ResponseEvent {
        return eventBuilder.redirectFor(event, payload)
    }

    fun error(
        type: EventErrorType,
        message: EventMessage,
    ): ResponseEvent {
        return eventBuilder.errorFor(event, type, message)
    }
}
