package br.com.guiabolso.events.client

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.http.FuelHttpClient
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.client.model.Response.UnexpectedError.FailedDependency
import br.com.guiabolso.events.client.model.Response.UnexpectedError.Timeout
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.metric.CompositeMetricReporter
import br.com.guiabolso.events.metric.MDCMetricReporter
import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.metric.NewRelicMetricReporter
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsResponseEvent
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventClient
@JvmOverloads
constructor(
        private val httpClient: HttpClientAdapter = FuelHttpClient(),
        private val reporter: MetricReporter = CompositeMetricReporter(MDCMetricReporter(), NewRelicMetricReporter()),
        private val defaultTimeout: Int = 60000) {

    companion object {
        private val logger = LoggerFactory.getLogger(EventClient::class.java)!!
    }

    @JvmOverloads
    fun sendEvent(url: String, requestEvent: RequestEvent, timeout: Int? = null): Response {
        try {
            logger.debug("Sending event ${requestEvent.name}:${requestEvent.version} to $url with timeout $timeout.")
            val rawResponse = httpClient.post(
                    url,
                    mapOf("Content-Type" to "application/json"),
                    MapperHolder.mapper.toJson(requestEvent),
                    Charsets.UTF_8,
                    timeout ?: defaultTimeout
            )
            val responseEvent = parseEvent(rawResponse)
            checkDeprecation(requestEvent, responseEvent)
            return if (responseEvent.isSuccess()) {
                logger.debug("Received success event response for ${requestEvent.name}:${requestEvent.version}.")
                Response.Success(responseEvent)
            } else {
                logger.debug("Received error event response for ${requestEvent.name}:${requestEvent.version}.")
                Response.Error(responseEvent, responseEvent.getErrorType())
            }
        } catch (e: TimeoutException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} timeout.", e)
            return Timeout(e)
        } catch (e: BadProtocolException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} bad protocol.", e)
            return FailedDependency(e, e.payload)
        } catch (e: Exception) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} error.", e)
            return FailedDependency(e)
        }
    }

    private fun parseEvent(rawResponse: String): ResponseEvent {
        try {
            val rawEvent = MapperHolder.mapper.fromJson(rawResponse, RawEvent::class.java)
            return validateAsResponseEvent(rawEvent)
        } catch (e: Exception) {
            throw BadProtocolException(rawResponse, e)
        }
    }

    private fun checkDeprecation(requestEvent: RequestEvent, responseEvent: ResponseEvent) {
        val (deprecated, description, deactivationDate) = responseEvent.deprecationDetails
        if (deprecated) {
            val date = deactivationDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "Not Informed"

            logger.warn("Event ${requestEvent.identification} was deprecated. It will be deactivated on '$date'. Deprecation details: $description.")
            reporter.addProperty("Deprecation", "DeprecatedEventUsed")

            if (deactivationDate != null) {
                val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deactivationDate).coerceAtLeast(0)
                reporter.addProperty("DeprecationDaysLeft", daysLeft)
            }
        }
    }

}