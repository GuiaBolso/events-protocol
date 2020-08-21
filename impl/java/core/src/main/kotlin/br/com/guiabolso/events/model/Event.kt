package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder.mapper
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

@Serializable
sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonElement
    abstract val identity: JsonObject
    abstract val auth: JsonObject
    abstract val metadata: JsonObject

    inline fun <reified T> payloadAs(): T = this.payload.convertTo()

    inline fun <reified T> identityAs(): T = this.identity.convertTo()

    inline fun <reified T> authAs(): T = this.auth.convertTo()

    val userId: Long?
        get() = this.identity["userId"]?.jsonPrimitive?.longOrNull

    val userIdAsString: String?
        get() = this.identity["userId"]?.jsonPrimitive?.contentOrNull

    val origin: String?
        get() = this.metadata["origin"]?.jsonPrimitive?.contentOrNull

    inline fun <reified T> JsonElement.convertTo(): T = mapper.decodeFromJsonElement(this)
}

@Serializable
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

@Serializable
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
