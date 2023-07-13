package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.BypassedException
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.tracing.Tracer

class BypassExceptionHandler(
    private val wrapExceptionAndEvent: Boolean
) : EventExceptionHandler<Exception> {

    override suspend fun handleException(
        exception: Exception,
        event: RequestEventContext,
        tracer: Tracer
    ): ResponseEvent {
        if (wrapExceptionAndEvent) {
            throw BypassedException(exception, event.event)
        } else {
            throw exception
        }
    }
}
