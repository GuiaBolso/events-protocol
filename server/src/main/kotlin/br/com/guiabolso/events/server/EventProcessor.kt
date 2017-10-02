package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.*
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.exception.ExceptionUtils.getStackTrace
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.server.metric.CompositeMetricReporter
import br.com.guiabolso.events.server.metric.MDCMetricReporter
import br.com.guiabolso.events.server.metric.MetricReporter
import br.com.guiabolso.events.server.metric.NewrelicMetricReporter
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import org.slf4j.LoggerFactory.getLogger

class EventProcessor
@JvmOverloads
constructor(
        private val discovery: EventHandlerDiscovery,
        private val exceptionHandlerRegistry: ExceptionHandlerRegistry = ExceptionHandlerRegistry(),
        private val reporter: MetricReporter = CompositeMetricReporter(MDCMetricReporter(), NewrelicMetricReporter())) {

    companion object {
        private val logger = getLogger(EventProcessor::class.java)!!
    }

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
                        reporter.startProcessingEvent(event)
                        handler.handle(event).json()
                    } catch (e: Exception) {
                        exceptionHandlerRegistry.handleException(e, event, reporter).json()
                    } finally {
                        EventContextHolder.clean()
                        reporter.eventProcessFinished(event)
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
                logger.error("Missing required property ${e.message}.", e)
                reporter.notifyError(e)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("missingProperty" to e.message)
                ))
            } catch (e: Exception) {
                logger.error("Error parsing event.", e)
                reporter.notifyError(e)
                badProtocol(EventMessage(
                        "INVALID_COMMUNICATION_PROTOCOL",
                        mapOf("message" to e.message, "exception" to getStackTrace(e))
                ))
            }

    private fun ResponseEvent.json() = MapperHolder.mapper.toJson(this)

}