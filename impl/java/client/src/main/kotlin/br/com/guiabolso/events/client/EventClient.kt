package br.com.guiabolso.events.client

import br.com.guiabolso.events.client.adapter.HttpClientAdapter
import br.com.guiabolso.events.client.exception.BadProtocolException
import br.com.guiabolso.events.client.exception.TimeoutException
import br.com.guiabolso.events.client.http.OkHttpClientAdapter
import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.client.model.toContext
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import org.slf4j.LoggerFactory

class EventClient
@JvmOverloads
constructor(
    private val jsonAdapter: JsonAdapter,
    private val httpClient: HttpClientAdapter = OkHttpClientAdapter(),
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
        return try {
            logger.debug("Sending event ${requestEvent.name}:${requestEvent.version} to $url with timeout $timeout.")
            val rawResponse = httpClient.post(
                url,
                customHeaders,
                jsonAdapter.toJson(requestEvent),
                Charsets.UTF_8,
                timeout ?: defaultTimeout
            )
            val event = parseEvent(rawResponse)
            when {
                event.isSuccess() -> {
                    logger.debug("Received success event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Success(event.toContext(jsonAdapter))
                }

                event.isRedirect() -> {
                    logger.debug("Received redirect event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Redirect(event.toContext(jsonAdapter))
                }

                else -> {
                    logger.debug("Received error event response for ${requestEvent.name}:${requestEvent.version}.")
                    Response.Error(event.toContext(jsonAdapter), event.getErrorType())
                }
            }
        } catch (e: TimeoutException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} timeout.", e)
            Response.Timeout(e)
        } catch (e: BadProtocolException) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} bad protocol.", e)
            Response.FailedDependency(e, e.payload)
        } catch (e: Exception) {
            logger.warn("Event ${requestEvent.name}:${requestEvent.version} error.", e)
            Response.FailedDependency(e)
        }
    }

    private fun parseEvent(rawResponse: String): ResponseEvent {
        try {
            val rawEvent = jsonAdapter.fromJson<RawEvent>(rawResponse)
            return eventValidator.validateAsResponseEvent(rawEvent)
        } catch (e: Exception) {
            throw BadProtocolException(rawResponse, e)
        }
    }
}
