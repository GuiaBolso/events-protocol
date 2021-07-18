package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventNotFoundException
import br.com.guiabolso.tracing.Tracer

object EventNotFoundExceptionHandler : EventExceptionHandler<EventNotFoundException> {

    override suspend fun handleException(
        exception: EventNotFoundException,
        event: RequestEvent,
        tracer: Tracer
    ): ResponseEvent {
        tracer.notifyError(exception, false)
        return eventNotFound(event)
    }
}
