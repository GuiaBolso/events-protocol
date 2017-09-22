package br.com.guiabolso.events.client.model

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent

sealed class Response {

    class Success(val event: ResponseEvent) : Response()

    class Error(val event: ResponseEvent, val errorType: EventErrorType) : Response()

    class FailedDependency(val exception: Exception) : Response()

    class Timeout(val exception: Exception) : Response()

    fun isSuccess() = this is Success

    fun isError() = this is Error

    fun isFailedDependency() = this is FailedDependency

    fun isTimeout() = this is Timeout

    fun <T : Response> getAs(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(this::class.java)) return clazz.cast(this)
        throw IllegalStateException("Invalid response type. This response is ${this.javaClass.simpleName} instead of ${clazz.simpleName}.")
    }

}