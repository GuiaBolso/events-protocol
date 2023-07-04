package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventNotFoundException
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.tracing.Tracer

class EventNotFoundExceptionHandler(
    private val eventBuilder: EventBuilder,
) : EventExceptionHandler<EventNotFoundException> {

    override suspend fun handleException(
        exception: EventNotFoundException,
        event: RequestEventContext,
        tracer: Tracer
    ): ResponseEvent {
        tracer.notifyError(exception, false)
        return eventBuilder.eventNotFound(event.event)
    }
}
