package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.*
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import br.com.guiabolso.metrics.MetricReporter
import br.com.guiabolso.metrics.factory.MetricReporterFactory
import br.com.guiabolso.metrics.utils.ExceptionUtils.getStackTrace

class EventProcessor
@JvmOverloads
constructor(
        private val discovery: EventHandlerDiscovery,
        private val exceptionHandlerRegistry: ExceptionHandlerRegistry = ExceptionHandlerRegistry(),
        private val reporter: MetricReporter = MetricReporterFactory.createMetricReporter(),
        private val exposeExceptions: Boolean = false) {

    fun <T : Throwable> register(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        exceptionHandlerRegistry.register(clazz, handler)
    }

    fun <T : Throwable> register(clazz: Class<T>, handler: (T, Event, MetricReporter) -> ResponseEvent) {
        exceptionHandlerRegistry.register(clazz, handler)
    }

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
                        exceptionHandlerRegistry.handleException(e, event, reporter).json()
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
                reporter.notifyError(e, false)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("missingProperty" to e.message)
                ))
            } catch (e: Exception) {
                reporter.notifyError(e, false)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("message" to e.message, "exception" to getStackTrace(e))
                ))
            }

    private fun startProcessingEvent(event: RequestEvent) {
        reporter.setOperationName("${event.name}:V${event.version}")
        reporter.addProperty("EventID", event.id)
        reporter.addProperty("FlowID", event.flowId)
        reporter.addProperty("UserID", event.identity.get("userId")?.asString ?: "unknown")
        reporter.addProperty("Origin", event.metadata.get("origin")?.asString ?: "unknown")
    }

    private fun eventProcessFinished() {
        reporter.clear()
    }

    private fun ResponseEvent.json() = MapperHolder.mapper.toJson(this)

}