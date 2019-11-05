package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import br.com.guiabolso.events.validation.withCheckedJsonNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject

sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonElement
    abstract val identity: JsonObject
    abstract val auth: JsonObject
    abstract val metadata: JsonObject

    fun <T> payloadAs(clazz: Class<T>): T = MapperHolder.mapper.fromJson(this.payload, clazz)

    inline fun <reified T> payloadAs(): T = MapperHolder.mapper.fromJson(this.payload, T::class.java)

    fun <T> identityAs(clazz: Class<T>): T = MapperHolder.mapper.fromJson(this.identity, clazz)

    inline fun <reified T> identityAs(): T = MapperHolder.mapper.fromJson(this.identity, T::class.java)

    fun <T> authAs(clazz: Class<T>): T = MapperHolder.mapper.fromJson(this.auth, clazz)

    inline fun <reified T> authAs(): T = MapperHolder.mapper.fromJson(this.auth, T::class.java)

    val userId: Long?
        get() = this.identity.withCheckedJsonNull("userId") {
            it.getAsJsonPrimitive("userId")?.asLong
        }

    val origin: String?
        get() = this.metadata.withCheckedJsonNull("origin") {
            it.getAsJsonPrimitive("origin")?.asString
        }

    companion object {
        const val DEFAULT_SUCCESS_SUFFIX = "response"
        const val REDIRECT_SUFFIX = "redirect"
        const val ACCEPTED_SUFFIX = "accepted"

        const val EVENT_NOT_FOUND_EVENT = "eventNotFound"
        const val BAD_PROTOCOL_EVENT = "badProtocol"
        val ERROR_EVENTS = listOf(EVENT_NOT_FOUND_EVENT, BAD_PROTOCOL_EVENT)
    }
}

data class ResponseEvent(
    override val name: String,
    override val version: Int,
    override val id: String,
    override val flowId: String,
    override val payload: JsonElement,
    override val identity: JsonObject,
    override val auth: JsonObject,
    override val metadata: JsonObject
) : Event() {

    fun isSuccess() = this.name.endsWith(":$DEFAULT_SUCCESS_SUFFIX") || isRedirect() || isAccepted()

    fun isRedirect() = this.name.endsWith(":$REDIRECT_SUFFIX")

    fun isAccepted() = this.name.endsWith(":$ACCEPTED_SUFFIX")

    fun isError() = !this.isSuccess() || this.name in ERROR_EVENTS

    fun getErrorType(): EventErrorType {
        check(!isSuccess()) { "This is not an error event." }
        return getErrorType(this.name.substringAfterLast(":"))
    }

    val sunset: EventSunset?
        get() = this.metadata.withCheckedJsonNull("sunset") {
            MapperHolder.mapper.fromJson(it.getAsJsonObject("sunset"), EventSunset::class.java)
        }

    val sunsetScheduled: Boolean
        get() = sunset != null

}

data class RequestEvent(
    override val name: String,
    override val version: Int,
    override val id: String,
    override val flowId: String,
    override val payload: JsonElement,
    override val identity: JsonObject,
    override val auth: JsonObject,
    override val metadata: JsonObject
) : Event()