package br.com.guiabolso.events.client.model

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent

sealed class Response {

    data class Success(val event: ResponseEvent) : Response()

    data class Error(val event: ResponseEvent, val errorType: EventErrorType) : Response()

    sealed class UnexpectedError : Response() {

        abstract val exception: Exception

        data class FailedDependency(override val exception: Exception, val response: String? = null) : UnexpectedError()

        data class Timeout(override val exception: Exception) : UnexpectedError()

    }

    fun isSuccess() = this is Success

    fun isError() = this is Error

    fun isUnexpectedError() = this is UnexpectedError

    fun isFailedDependency() = this is UnexpectedError.FailedDependency

    fun isTimeout() = this is UnexpectedError.Timeout

    fun <T : Response> getAs(clazz: Class<T>): T {
        if (clazz.isAssignableFrom(this::class.java)) return clazz.cast(this)
        throw IllegalStateException("Invalid response type. This response is ${this.javaClass.simpleName} instead of ${clazz.simpleName}.")
    }

}