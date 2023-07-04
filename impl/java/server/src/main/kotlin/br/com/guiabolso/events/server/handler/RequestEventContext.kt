package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.builder.EventTemplate
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.model.User

data class RequestEventContext(
    val event: RequestEvent,
    val jsonAdapter: JsonAdapter,
) {
    private val eventBuilder = EventBuilder(jsonAdapter)

    val user: User? get() = event.user
    val userId: Long? get() = event.userId
    val userIdAsString: String? get() = event.userIdAsString
    val origin: String? get() = event.origin

    fun <T> payloadAs(clazz: Class<T>): T = event.payloadAs(clazz, jsonAdapter)
    inline fun <reified T> payloadAs(): T = event.payloadAs(jsonAdapter)

    fun <T> identityAs(clazz: Class<T>): T = this.event.identityAs(clazz, jsonAdapter)
    inline fun <reified T> identityAs(jsonAdapter: JsonAdapter): T = this.event.identityAs(jsonAdapter)

    fun <T> authAs(clazz: Class<T>): T = this.event.authAs(clazz, jsonAdapter)
    inline fun <reified T> authAs(): T = this.event.authAs(jsonAdapter)

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
