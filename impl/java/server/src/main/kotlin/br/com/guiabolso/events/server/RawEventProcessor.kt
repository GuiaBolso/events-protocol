package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventThreadContextManager.withContext
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidationException
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer

class RawEventProcessor
@JvmOverloads
constructor(
    private val discovery: EventHandlerDiscovery,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tracer: Tracer = DefaultTracer,
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
                        withContext(EventContext(event.id, event.flowId)).use {
                            startProcessingEvent(event)
                            handler.handle(event)
                        }
                    } catch (e: Exception) {
                        exceptionHandlerRegistry.handleException(e, event, tracer)
                    } finally {
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
