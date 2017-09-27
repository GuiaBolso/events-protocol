package br.com.guiabolso.events.client

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.http.FuelHttpClient
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.utils.Events.getErrorType
import br.com.guiabolso.events.utils.Events.isSuccess
import br.com.guiabolso.events.validation.EventValidator.validateAsResponseEvent
import org.slf4j.LoggerFactory

class EventClient(private val httpClient: HttpClientAdapter = FuelHttpClient()) {

    companion object {
        private val logger = LoggerFactory.getLogger(EventClient::class.java)!!
    }

    fun sendEvent(url: String, requestEvent: RequestEvent, timeout: Int? = null): Response {
        try {
            logger.debug("Sending event ${requestEvent.name}:${requestEvent.version} to $url with timeout $timeout.")
            val rawResponse = httpClient.post(
                    url,
                    mapOf("Content-Type" to "application/json"),
                    MapperHolder.mapper.toJson(requestEvent),
                    Charsets.UTF_8,
                    timeout
            )
            val event = parseEvent(rawResponse)
            return if (event.isSuccess()) {
                logger.debug("Received success event response for ${requestEvent.name}:${requestEvent.version}.")
                Response.Success(event)
            } else {
                logger.debug("Received error event response for ${requestEvent.name}:${requestEvent.version}.")
                Response.Error(event, event.getErrorType())
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
            val rawEvent = MapperHolder.mapper.fromJson(rawResponse, RawEvent::class.java)
            return validateAsResponseEvent(rawEvent)
        } catch (e: Exception) {
            throw BadProtocolException(rawResponse, e)
        }
    }

}