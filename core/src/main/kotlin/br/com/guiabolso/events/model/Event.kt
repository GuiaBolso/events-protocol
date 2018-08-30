package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.validation.withCheckedJsonNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.IllegalStateException

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

    fun isSuccess() = this.name.endsWith(":response")

    fun isError() = !this.isSuccess()

    fun getErrorType(): EventErrorType {
        if (isSuccess()) throw IllegalStateException("This is not an error event.")
        return EventErrorType.getErrorType(this.name.substringAfterLast(":"))
    }

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