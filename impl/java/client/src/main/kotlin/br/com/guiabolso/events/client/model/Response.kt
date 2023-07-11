package br.com.guiabolso.events.client.model

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.model.AbstractEventContext
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent

data class ResponseEventContext(
    override val event: ResponseEvent,
    override val jsonAdapter: JsonAdapter,
) : AbstractEventContext<ResponseEvent>()

sealed class Response {

    data class Success(val event: ResponseEventContext) : Response()

    data class Redirect(val event: ResponseEventContext) : Response()

    data class Error(val event: ResponseEventContext, val errorType: EventErrorType) : Response()

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
