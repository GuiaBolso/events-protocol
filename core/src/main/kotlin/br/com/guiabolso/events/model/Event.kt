package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.MapperHolder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.lang.IllegalStateException
import java.time.LocalDate


sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonElement
    abstract val identity: JsonObject
    abstract val auth: JsonObject
    abstract val metadata: JsonObject

    val identification: String
        get() = "$id:V$version"

    val userId: Long?
        get() = this.identity.getAsJsonPrimitive("userId")?.asLong

    val origin: String
        get() = this.metadata.getAsJsonPrimitive("origin")?.asString ?: "Unknown"

    val deprecationDetails: DeprecationDetails
        get() {
            val details = MapperHolder.mapper.fromJson(this.metadata.getAsJsonObject("deprecationDetails"), DeprecationDetails::class.java)
            if (details != null) {
                return details
            }
            return DeprecationDetails(false, null, null)
        }

    fun <T> payloadAs(clazz: Class<T>): T = MapperHolder.mapper.fromJson(this.payload, clazz)

    inline fun <reified T> payloadAs(): T = MapperHolder.mapper.fromJson(this.payload, T::class.java)

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

    fun getErrorMessage(): EventMessage {
        if (isSuccess()) throw IllegalStateException("This is not an error event.")
        return payloadAs()
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

data class DeprecationDetails(
        val deprecated: Boolean = false,
        val description: String? = null,
        val deactivationDate: LocalDate? = null
)