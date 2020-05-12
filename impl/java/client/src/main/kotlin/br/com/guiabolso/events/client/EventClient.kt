package br.com.guiabolso.events.client

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.http.FuelHttpClient
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import org.slf4j.LoggerFactory

class EventClient
@JvmOverloads
constructor(
    private val httpClient: HttpClientAdapter = FuelHttpClient(),
    private val eventValidator: EventValidator = StrictEventValidator(),
    private val defaultTimeout: Int = 60000
) {

    companion object {
        private val logger = LoggerFactory.getLogger(EventClient::class.java)!!
    }

    @JvmOverloads
    fun sendEvent(
        url: String,
        requestEvent: RequestEvent,
        headers: Map<String, String> = emptyMap(),
        timeout: Int? = null
    ): Response {
        val customHeaders = HashMap(headers).apply { this["Content-Type"] = "application/json" }
        try {
            logger.debug("Sending event ${requestEvent.name}:${requestEvent.version} to $url with timeout $timeout.")
            val rawResponse = httpClient.post(
                url,
                customHeaders,
                mapper.toJson(requestEvent),
                Charsets.UTF_8,
                timeout ?: defaultTimeout
            )
            val event = parseEvent(rawResponse)
            return when {
                event.isSuccess() -> {
                    logger.debug("Received success event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Success(event)
                }
                event.isRedirect() -> {
                    logger.debug("Received redirect event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Redirect(event)
                }
                else -> {
                    logger.debug("Received error event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Error(event, event.getErrorType())
                }
            }
        } catch (e: TimeoutException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} timeout.", e)
            return Response.Timeout(e)
        } catch (e: BadProtocolException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} bad protocol.", e)
            return Response.FailedDependency(e, e.payload)
        } catch (e: Exception) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} error.", e)
            return Response.FailedDependency(e)
        }
    }

    private fun parseEvent(rawResponse: String): ResponseEvent {
        try {
            val rawEvent = mapper.fromJson(rawResponse, RawEvent::class.java)
            return eventValidator.validateAsResponseEvent(rawEvent)
        } catch (e: Exception) {
            throw BadProtocolException(rawResponse, e)
        }
    }
}
