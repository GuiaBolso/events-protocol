package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.bypassExceptionHandler
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.factory.TracerFactory

class EventProcessor
@JvmOverloads
constructor(
    private val discovery: EventHandlerDiscovery,
    private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tracer: Tracer = TracerFactory.createTracer(),
    private val eventValidator: EventValidator = StrictEventValidator()
) {

    @JvmOverloads
    @Deprecated(
        "The 'exposeExceptions' should not be used. Use 'ExceptionHandlerRegistryFactory.bypassExceptionHandler()' as exception handler instead.",
        ReplaceWith(
            "EventProcessor(discovery, ExceptionHandlerRegistryFactory.bypassExceptionHandler(), eventValidator, tracer)",
            "br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory"
        )
    )
    constructor(
        discovery: EventHandlerDiscovery,
        exceptionHandlerRegistry: ExceptionHandlerRegistry,
        tracer: Tracer = TracerFactory.createTracer(),
        eventValidator: EventValidator = StrictEventValidator(),
        exposeExceptions: Boolean
    ) : this(discovery, configureExceptionHandler(exceptionHandlerRegistry, exposeExceptions), tracer, eventValidator)

    fun processEvent(rawEvent: String): String {
        return when (val event = parseAndValidateEvent(rawEvent)) {
            is RequestEvent -> {
                val handler = discovery.eventHandlerFor(event.name, event.version)
                return if (handler == null) {
                    eventNotFound(event).json()
                } else {
                    try {
                        EventContextHolder.setContext(EventContext(event.id, event.flowId))
                        startProcessingEvent(event)
                        handler.handle(event).json()
                    } catch (e: Exception) {
                        exceptionHandlerRegistry.handleException(e, event, tracer).json()
                    } finally {
                        EventContextHolder.clean()
                        eventProcessFinished()
                    }
                }
            }
            is ResponseEvent -> event.json()
        }
    }

    private fun parseAndValidateEvent(rawEvent: String): Event =
        try {
            val input = MapperHolder.mapper.fromJson(rawEvent, RawEvent::class.java)
            eventValidator.validateAsRequestEvent(input)
        } catch (e: IllegalArgumentException) {
            tracer.notifyError(e, false)
            badProtocol(
                EventMessage(
                    "INVALID_COMMUNICATION_PROTOCOL",
                    mapOf("missingProperty" to e.message)
                )
            )
        } catch (e: Exception) {
            tracer.notifyError(e, false)
            badProtocol(
                EventMessage(
                    "INVALID_COMMUNICATION_PROTOCOL",
                    mapOf("message" to e.message)
                )
            )
        }

    private fun startProcessingEvent(event: RequestEvent) {
        tracer.setOperationName("${event.name}:V${event.version}")
        tracer.addProperty("EventID", event.id)
        tracer.addProperty("FlowID", event.flowId)
        tracer.addProperty("UserID", event.userId?.toString() ?: "unknown")
        tracer.addProperty("Origin", event.origin ?: "unknown")
    }

    private fun eventProcessFinished() {
        tracer.clear()
    }

    private fun ResponseEvent.json() = MapperHolder.mapper.toJson(this)

    companion object {

        private fun configureExceptionHandler(
            handler: ExceptionHandlerRegistry,
            exposeExceptions: Boolean
        ): ExceptionHandlerRegistry {
            return if (exposeExceptions) {
                bypassExceptionHandler(false)
            } else {
                handler
            }
        }

    }
}