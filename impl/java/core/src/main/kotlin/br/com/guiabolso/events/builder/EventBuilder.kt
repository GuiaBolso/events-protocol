package br.com.guiabolso.events.builder

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.EventErrorType.EventNotFound
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import java.util.UUID

class EventBuilder(private val jsonAdapter: JsonAdapter) {

    fun event(operations: EventTemplate.() -> Unit): RequestEvent {
        return EventTemplate(jsonAdapter).apply(operations).toRequestEvent()
    }

    fun responseEvent(
        operations: EventTemplate.() -> Unit,
    ): ResponseEvent {
        return EventTemplate(jsonAdapter).apply(operations).toResponseEvent()
    }

    suspend fun responseFor(
        event: RequestEvent,
        operations: suspend EventTemplate.() -> Unit,
    ): ResponseEvent {
        return EventTemplate(jsonAdapter).apply {
            operations()

            name = "${event.name}:response"
            version = event.version
            id = id ?: event.id
            flowId = flowId ?: event.flowId
        }.toResponseEvent()
    }

    fun errorFor(
        event: RequestEvent,
        type: EventErrorType,
        message: EventMessage,
    ): ResponseEvent {
        if (type is EventErrorType.Unknown) {
            throw IllegalArgumentException(
                "This error type should not be used to send events. This error error type only exists to provide " +
                        "future compatibility with newer versions of this API."
            )
        }

        return EventTemplate(jsonAdapter).apply {
            this.name = "${event.name}:${type.typeName}"
            this.version = event.version
            this.payload = message
            this.id = this.id ?: event.id
            this.flowId = this.flowId ?: event.flowId
        }.toResponseEvent()
    }

    fun redirectFor(
        requestEvent: RequestEvent,
        payload: RedirectPayload,
    ): ResponseEvent {
        return EventTemplate(jsonAdapter).apply {
            this.name = "${requestEvent.name}:redirect"
            this.version = requestEvent.version
            this.payload = payload
            this.id = id ?: requestEvent.id
            this.flowId = flowId ?: requestEvent.flowId
        }.toResponseEvent()
    }

    fun eventNotFound(event: RequestEvent): ResponseEvent {
        return EventTemplate(jsonAdapter).apply {
            this.name = EventNotFound.typeName
            this.version = 1
            this.id = id ?: event.id
            this.flowId = flowId ?: event.flowId
            this.payload = EventMessage(
                code = "NO_EVENT_HANDLER_FOUND",
                parameters = mapOf("event" to event.name, "version" to event.version)
            )
        }.toResponseEvent()
    }

    fun badProtocol(message: EventMessage): ResponseEvent {
        return EventTemplate(jsonAdapter).apply {
            name = BadProtocol.typeName
            version = 1
            id = UUID.randomUUID().toString()
            flowId = UUID.randomUUID().toString()
            payload = message
        }.toResponseEvent()
    }
}
