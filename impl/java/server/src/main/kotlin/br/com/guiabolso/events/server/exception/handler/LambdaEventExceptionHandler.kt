package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.tracing.Tracer

class LambdaEventExceptionHandler<in T : Throwable>(
    private val func: suspend (T, RequestEventContext, Tracer) -> ResponseEvent
) : EventExceptionHandler<T> {

    override suspend fun handleException(exception: T, event: RequestEventContext, tracer: Tracer) =
        func(exception, event, tracer)
}
