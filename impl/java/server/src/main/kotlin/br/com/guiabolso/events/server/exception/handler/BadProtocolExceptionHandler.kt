package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.tracing.Tracer

object BadProtocolExceptionHandler : EventExceptionHandler<EventValidationException> {

    override suspend fun handleException(
        exception: EventValidationException,
        event: RequestEvent,
        tracer: Tracer
    ): ResponseEvent {
        tracer.notifyError(exception, false)
        return badProtocol(
            EventMessage(
                "INVALID_COMMUNICATION_PROTOCOL",
                mapOf("propertyName" to exception.propertyName)
            )
        )
    }
}
