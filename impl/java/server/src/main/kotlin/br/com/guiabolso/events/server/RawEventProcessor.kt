package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.validation.EventValidationException
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.factory.TracerFactory

class RawEventProcessor
@JvmOverloads
constructor(
    private val discovery: EventHandlerDiscovery,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tracer: Tracer = TracerFactory.createTracer(),
    private val eventValidator: EventValidator = StrictEventValidator()
) {

    fun processEvent(rawEvent: RawEvent?): ResponseEvent {
        return when (val event = validateEvent(rawEvent)) {
            is RequestEvent -> {
                val handler = discovery.eventHandlerFor(event.name, event.version)
                return if (handler == null) {
                    eventNotFound(event)
                } else {
                    try {
                        EventContextHolder.setContext(EventContext(event.id, event.flowId))
                        startProcessingEvent(event)
                        handler.handle(event)
                    } catch (e: Exception) {
                        exceptionHandlerRegistry.handleException(e, event, tracer)
                    } finally {
                        EventContextHolder.clean()
                        eventProcessFinished()
                    }
                }
            }
            is ResponseEvent -> event
        }
    }

    private fun validateEvent(rawEvent: RawEvent?): Event =
        try {
            eventValidator.validateAsRequestEvent(rawEvent)
        } catch (e: EventValidationException) {
            tracer.notifyError(e, false)
            badProtocol(e.eventMessage)
        }

    private fun startProcessingEvent(event: RequestEvent) {
        tracer.setOperationName("${event.name}:V${event.version}")
        tracer.addProperty("EventID", event.id)
        tracer.addProperty("FlowID", event.flowId)
        tracer.addProperty("UserID", event.userIdAsString ?: "unknown")
        tracer.addProperty("Origin", event.origin ?: "unknown")
    }

    private fun eventProcessFinished() {
        tracer.clear()
    }
}
