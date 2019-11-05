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
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class EventClient
@JvmOverloads
constructor(
    private val httpClient: HttpClientAdapter = FuelHttpClient(),
    private val eventValidator: EventValidator = StrictEventValidator(),
    private val defaultTimeout: Int = 60000
) {

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
            checkForSunsetData(requestEvent, responseEvent)
            return when {
                responseEvent.isRedirect() -> {
                    logger.debug("Received redirect event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Success(responseEvent)
                }
                responseEvent.isAccepted() -> {
                    logger.debug("Received accepted event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Success(responseEvent)
                }
                responseEvent.isSuccess() -> {
                    logger.debug("Received success event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Success(responseEvent)
                }
                else -> {
                    logger.debug("Received error event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Error(responseEvent, responseEvent.getErrorType())
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
            val rawEvent = MapperHolder.mapper.fromJson(rawResponse, RawEvent::class.java)
            return eventValidator.validateAsResponseEvent(rawEvent)
        } catch (e: Exception) {
            throw BadProtocolException(rawResponse, e)
        }
    }

    private fun checkForSunsetData(requestEvent: RequestEvent, responseEvent: ResponseEvent) {
        val sunset = responseEvent.sunset
        if (sunset != null) {
            if (sunset.date >= LocalDateTime.now()) {
                logger.warn("The event ${requestEvent.name}:V${requestEvent.version} has a sunset scheduled to ${sunset.date}. Description: ${sunset.description}")
            } else {
                logger.error("The event ${requestEvent.name}:V${requestEvent.version} is not supported anymore and should be replaced. Description: ${sunset.description}")
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(EventClient::class.java)!!
    }

}