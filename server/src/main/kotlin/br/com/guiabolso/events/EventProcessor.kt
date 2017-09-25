package br.com.guiabolso.events

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.notFoundFor
import br.com.guiabolso.events.exception.ExceptionHandlerRegistry.handleException
import br.com.guiabolso.events.handler.EventHandlerDiscovery
import br.com.guiabolso.events.metric.CompositeMetricReporter
import br.com.guiabolso.events.metric.MDCMetricReporter
import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.metric.NewrelicMetricReporter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import com.google.gson.Gson
import org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace
import org.slf4j.LoggerFactory.getLogger

class EventProcessor(
        private val discovery: EventHandlerDiscovery,
        private val reporter: MetricReporter = CompositeMetricReporter(MDCMetricReporter(), NewrelicMetricReporter())) {

    companion object {
        private val logger = getLogger(EventProcessor::class.java)!!
        private val mapper = Gson()
    }

    fun processEvent(rawEvent: String): Event {
        val event = parseAndValidateEvent(rawEvent)
        val handler = discovery.eventHandlerFor(event.name, event.version)

        return if (handler == null) {
            notFoundFor(event)
        } else {
            try {
                reporter.startProcessingEvent(event)
                handler.handle(event)
            } catch (e: Exception) {
                handleException(e, event, reporter)
            } finally {
                reporter.eventProcessFinished(event)
            }
        }
    }

    private fun parseAndValidateEvent(rawEvent: String): Event =
            try {
                val input = mapper.fromJson(rawEvent, RawEvent::class.java)
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

}