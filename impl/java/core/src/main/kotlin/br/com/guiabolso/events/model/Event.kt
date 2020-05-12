package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.validation.withCheckedJsonNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonElement
    abstract val identity: JsonObject
    abstract val auth: JsonObject
    abstract val metadata: JsonObject

    fun <T> payloadAs(clazz: Class<T>): T = this.payload.convertTo(clazz)

    inline fun <reified T> payloadAs(): T = this.payload.convertTo()

    fun <T> identityAs(clazz: Class<T>): T = this.identity.convertTo(clazz)

    inline fun <reified T> identityAs(): T = this.identity.convertTo()

    fun <T> authAs(clazz: Class<T>): T = this.auth.convertTo(clazz)

    inline fun <reified T> authAs(): T = this.auth.convertTo()

    val userId: Long?
        get() = this.identity.withCheckedJsonNull("userId") {
            it.getAsJsonPrimitive("userId")?.asLong
        }

    val userIdAsString: String?
        get() = this.identity.withCheckedJsonNull("userId") {
            it.getAsJsonPrimitive("userId")?.asString
        }

    val origin: String?
        get() = this.metadata.withCheckedJsonNull("origin") {
            it.getAsJsonPrimitive("origin")?.asString
        }

    inline fun <reified T> JsonElement.convertTo(): T = MapperHolder.mapper.fromJson(this, object : TypeToken<T>() {}.type)

    private fun <T> JsonElement.convertTo(clazz: Class<T>): T = MapperHolder.mapper.fromJson(this, clazz)
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

    fun isRedirect() = this.name.endsWith(":redirect")

    fun isError() = !this.isSuccess() && !this.isRedirect()

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
