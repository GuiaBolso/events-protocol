package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.*
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.factory.TracerFactory
import br.com.guiabolso.tracing.utils.ExceptionUtils.getStackTrace

class EventProcessor
@JvmOverloads
constructor(
        private val discovery: EventHandlerDiscovery,
        private val exceptionHandlerRegistry: ExceptionHandlerRegistry,
        private val tracer: Tracer = TracerFactory.createTracer(),
        private val exposeExceptions: Boolean = false) {

    fun processEvent(rawEvent: String): String {
        val event = parseAndValidateEvent(rawEvent)

        return when (event) {
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
                        if (exposeExceptions) {
                            throw e
                        }
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
                validateAsRequestEvent(input)
            } catch (e: IllegalArgumentException) {
                tracer.notifyError(e, false)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("missingProperty" to e.message)
                ))
            } catch (e: Exception) {
                tracer.notifyError(e, false)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("message" to e.message, "exception" to getStackTrace(e))
                ))
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

}