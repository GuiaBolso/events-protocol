package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.tracing.Tracer

class LambdaEventExceptionHandler<T : Throwable>(
    override val targetException: Class<T>,
    private val func: (T, RequestEvent, Tracer) -> ResponseEvent
) : EventExceptionHandler<T> {

    override fun handleException(exception: T, event: RequestEvent, tracer: Tracer) = func(exception, event, tracer)

}