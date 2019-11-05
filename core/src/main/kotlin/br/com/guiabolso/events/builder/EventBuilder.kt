package br.com.guiabolso.events.builder

import br.com.guiabolso.events.exception.MissingEventInformationException
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.Event.Companion.ACCEPTED_SUFFIX
import br.com.guiabolso.events.model.Event.Companion.BAD_PROTOCOL_EVENT
import br.com.guiabolso.events.model.Event.Companion.DEFAULT_SUCCESS_SUFFIX
import br.com.guiabolso.events.model.Event.Companion.EVENT_NOT_FOUND_EVENT
import br.com.guiabolso.events.model.Event.Companion.REDIRECT_SUFFIX
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.utils.EventUtils
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import java.util.UUID

class EventBuilder {

    companion object {

        @JvmStatic
        fun event(operations: EventBuilder.() -> Unit): RequestEvent {
            val builder = EventBuilder()

            builder.operations()

            return builder.buildRequestEvent()
        }

        @JvmStatic
        fun responseEvent(operations: EventBuilder.() -> Unit): ResponseEvent {
            val builder = EventBuilder()

            builder.operations()

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun responseFor(event: RequestEvent, operations: EventBuilder.() -> Unit): ResponseEvent {
            val builder = javaResponseFor(event)

            builder.operations()

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun javaResponseFor(event: RequestEvent): EventBuilder {
            val builder = EventBuilder()

            builder.name = "${event.name}:$DEFAULT_SUCCESS_SUFFIX"
            builder.version = event.version
            builder.id = builder.id ?: event.id
            builder.flowId = builder.flowId ?: event.flowId

            return builder
        }

        @JvmStatic
        fun acceptedFor(event: RequestEvent): ResponseEvent {
            val builder = EventBuilder()

            builder.name = "${event.name}:$ACCEPTED_SUFFIX"
            builder.version = event.version
            builder.id = builder.id ?: event.id
            builder.flowId = builder.flowId ?: event.flowId

            return builder.buildResponseEvent()
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
        fun redirectFor(requestEvent: RequestEvent, payload: RedirectPayload): ResponseEvent {
            val builder = EventBuilder()

            builder.name = "${requestEvent.name}:$REDIRECT_SUFFIX"
            builder.version = requestEvent.version
            builder.payload = payload
            builder.id = builder.id ?: requestEvent.id
            builder.flowId = builder.flowId ?: requestEvent.flowId

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun eventNotFound(event: RequestEvent): ResponseEvent {
            val builder = EventBuilder()

            builder.name = EVENT_NOT_FOUND_EVENT
            builder.version = 1
            builder.id = builder.id ?: event.id
            builder.flowId = builder.flowId ?: event.flowId
            builder.payload = EventMessage(
                "NO_EVENT_HANDLER_FOUND",
                mapOf("event" to event.name, "version" to event.version)
            )

            return builder.buildResponseEvent()
        }

        @JvmStatic
        fun badProtocol(message: EventMessage): ResponseEvent {
            val builder = EventBuilder()

            builder.name = BAD_PROTOCOL_EVENT
            builder.version = 1
            builder.id = UUID.randomUUID().toString()
            builder.flowId = UUID.randomUUID().toString()
            builder.payload = message

            return builder.buildResponseEvent()
        }

    }

    var name: String? = null
    var version: Int? = null
    var id = EventUtils.eventId
    var flowId = EventUtils.flowId
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

    private fun convertPayload() = when (this.payload) {
        null -> JsonNull.INSTANCE
        else -> MapperHolder.mapper.toJsonTree(this.payload)
    }

    private fun convertToJsonObjectOrEmpty(value: Any?) = when (value) {
        null -> JsonObject()
        JsonNull.INSTANCE -> JsonObject()
        else -> MapperHolder.mapper.toJsonTree(value).asJsonObject
    }

}

