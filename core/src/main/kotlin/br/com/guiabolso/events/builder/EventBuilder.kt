package br.com.guiabolso.events.builder

import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.exception.MissingEventInformationException
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

class EventBuilder {

    companion object {

        @JvmStatic
        fun event(operations: EventBuilder.() -> Unit): Event {
            val builder = EventBuilder()
            builder.operations()
            return builder.buildRequestEvent()
        }

        @JvmStatic
        fun responseFor(event: RequestEvent, operations: EventBuilder.() -> Unit): Event {
            val builder = EventBuilder()

            javaResponseFor(event)
            builder.operations()

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun javaResponseFor(event: RequestEvent): EventBuilder {
            val builder = EventBuilder()

            builder.name = "${event.name}:response"
            builder.version = event.version
            builder.id = builder.id ?: event.id
            builder.flowId = builder.flowId ?: event.flowId

            return builder
        }

        @JvmStatic
        fun errorFor(event: RequestEvent, type: EventErrorType, message: EventMessage): ResponseEvent {
            if (type is EventErrorType.Unknown) throw IllegalArgumentException("This error type should not be used to send events. This error error type only exists to provide future compatibility with newer versions of this API.")

            val builder = EventBuilder()
            builder.name = "${event.name}:${type.typeName}"
            builder.version = event.version
            builder.payload = message
            builder.id = builder.id ?: event.id
            builder.flowId = builder.flowId ?: event.flowId

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun eventNotFound(name: String, version: Int): ResponseEvent {
            val builder = EventBuilder()
            builder.name = "eventNotFound"
            builder.version = 1
            builder.id = UUID.randomUUID().toString()
            builder.flowId = UUID.randomUUID().toString()
            builder.payload = EventMessage("NO_EVENT_HANDLER_FOUND", mapOf("event" to name, "version" to version))
            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun badProtocol(message: EventMessage): ResponseEvent {
            val builder = EventBuilder()
            builder.name = "badProtocol"
            builder.version = 1
            builder.id = UUID.randomUUID().toString()
            builder.flowId = UUID.randomUUID().toString()
            builder.payload = message
            return builder.buildResponseEvent()
        }

    }

    private val context = EventContextHolder.getContext()
    var name: String? = null
    var version: Int? = null
    var id = context?.id
    var flowId = context?.flowId
    var payload: Any? = null
    var identity: Any? = null
    var auth: Any? = null
    var metadata: Any? = null

    fun buildRequestEvent() = RequestEvent(
            name = this.name ?: throw MissingEventInformationException("Missing event name."),
            version = this.version ?: throw MissingEventInformationException("Missing event version."),
            id = this.id ?: throw MissingEventInformationException("Missing event id."),
            flowId = this.flowId ?: throw MissingEventInformationException("Missing event flowId."),
            payload = convertPayload(),
            identity = convertToJsonObjectOrEmpty(this.identity),
            auth = convertToJsonObjectOrEmpty(this.auth),
            metadata = convertToJsonObjectOrEmpty(this.metadata)
    )

    fun buildResponseEvent() = ResponseEvent(
            name = this.name ?: throw MissingEventInformationException("Missing event name."),
            version = this.version ?: throw MissingEventInformationException("Missing event version."),
            id = this.id ?: throw MissingEventInformationException("Missing event id."),
            flowId = this.flowId ?: throw MissingEventInformationException("Missing event flowId."),
            payload = convertPayload(),
            identity = convertToJsonObjectOrEmpty(this.identity),
            auth = convertToJsonObjectOrEmpty(this.auth),
            metadata = convertToJsonObjectOrEmpty(this.metadata)
    )

    private fun convertPayload(): JsonElement {
        if (this.payload == null) throw MissingEventInformationException("Missing event payload.")
        return MapperHolder.mapper.toJsonTree(this.payload)
    }

    private fun convertToJsonObjectOrEmpty(value: Any?): JsonObject {
        return if (value == null) {
            JsonObject()
        } else {
            MapperHolder.mapper.toJsonTree(value).asJsonObject
        }
    }

}

