package br.com.guiabolso.events.client.model

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent

sealed class Response {

    sealed class DeserializableResponse : Response() {
        abstract val event: ResponseEvent
        abstract val jsonAdapter: JsonAdapter

        fun <T> payloadAs(clazz: Class<T>): T = event.payloadAs(clazz, jsonAdapter)
        inline fun <reified T> payloadAs(): T = event.payloadAs(jsonAdapter)

        fun <T> identityAs(clazz: Class<T>): T = event.identityAs(clazz, jsonAdapter)
        inline fun <reified T> identityAs(jsonAdapter: JsonAdapter): T = event.identityAs(jsonAdapter)

        fun <T> authAs(clazz: Class<T>): T = event.authAs(clazz, jsonAdapter)
        inline fun <reified T> authAs(): T = event.authAs(jsonAdapter)
    }

    data class Success(
        override val event: ResponseEvent,
        override val jsonAdapter: JsonAdapter,
    ) : DeserializableResponse()

    data class Redirect(
        override val event: ResponseEvent,
        override val jsonAdapter: JsonAdapter,
    ) : DeserializableResponse()

    data class Error(
        override val event: ResponseEvent,
        val errorType: EventErrorType,
        override val jsonAdapter: JsonAdapter,
    ) : DeserializableResponse()

    data class FailedDependency(val exception: Exception, val response: String? = null) : Response()

    data class Timeout(val exception: Exception) : Response()

    fun isSuccess() = this is Success

    fun isRedirect() = this is Redirect

    fun isError() = this is Error

    fun isFailedDependency() = this is FailedDependency

    fun isTimeout() = this is Timeout

    fun <T : Response> getAs(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(this::class.java)) return clazz.cast(this)
        throw IllegalStateException(
            "Invalid response type. This response is ${this.javaClass.simpleName} instead of ${clazz.simpleName}."
        )
    }
}
