package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.tracing.Tracer

interface EventExceptionHandler<T : Throwable> {

    val targetException: Class<T>

    fun handleException(exception: T, event: RequestEvent, tracer: Tracer): ResponseEvent

}