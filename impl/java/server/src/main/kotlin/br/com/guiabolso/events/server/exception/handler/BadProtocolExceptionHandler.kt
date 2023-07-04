package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.tracing.Tracer

class BadProtocolExceptionHandler(
    private val eventBuilder: EventBuilder
) : EventExceptionHandler<EventValidationException> {

    override suspend fun handleException(
        exception: EventValidationException,
        event: RequestEventContext,
        tracer: Tracer,
    ): ResponseEvent {
        tracer.notifyError(exception, false)
        return eventBuilder.badProtocol(
            EventMessage(
                "INVALID_COMMUNICATION_PROTOCOL",
                mapOf("propertyName" to exception.propertyName)
            )
        )
    }
}
