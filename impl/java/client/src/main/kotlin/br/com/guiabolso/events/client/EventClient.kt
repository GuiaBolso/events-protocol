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
    private val defaultTimeout: Int = 15000
) {

    companion object {
        private val logger = LoggerFactory.getLogger(EventClient::class.java)!!
    }

    @JvmOverloads
    suspend fun suspendSendEvent(
        url: String,
        requestEvent: RequestEvent,
        headers: Map<String, String> = emptyMap(),
        timeout: Int? = null
    ): Response {
        val customHeaders = HashMap(headers).apply { this["Content-Type"] = "application/json" }
        logger.debug("Sending event {}:{} to {} with timeout {}", requestEvent.name, requestEvent.version, url, timeout)

        val rawResponse = httpClient.runCatching {
            suspendPost(
                url = url,
                headers = customHeaders,
                payload = jsonAdapter.toJson(requestEvent),
                charset = Charsets.UTF_8,
                timeout = timeout ?: defaultTimeout
            )
        }

        return handleResponse(rawResponse, requestEvent)
    }

    @JvmOverloads
    fun sendEvent(
        url: String,
        requestEvent: RequestEvent,
        headers: Map<String, String> = emptyMap(),
        timeout: Int? = null
    ): Response {
        val customHeaders = HashMap(headers).apply { this["Content-Type"] = "application/json" }
        logger.debug("Sending event {}:{} to {} with timeout {}", requestEvent.name, requestEvent.version, url, timeout)
        val rawResponse = httpClient.runCatching {
            post(
                url = url,
                headers = customHeaders,
                payload = jsonAdapter.toJson(requestEvent),
                charset = Charsets.UTF_8,
                timeout = timeout ?: defaultTimeout
            )
        }

        return handleResponse(rawResponse, requestEvent)
    }

    private fun handleResponse(result: Result<String>, requestEvent: RequestEvent): Response = try {
        handleEventResponse(parseEvent(result.getOrThrow()))
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

    private fun handleEventResponse(event: ResponseEvent): Response = when {
        event.isSuccess() -> Response.Success(event.toContext(jsonAdapter)).also {
            logger.debug("Received success event response for {}:{}", it.event.name, it.event.version)
        }

        event.isRedirect() -> Response.Redirect(event.toContext(jsonAdapter)).also {
            logger.debug("Received redirect event response for {}:{}", it.event.name, it.event.version)
        }

        else -> Response.Error(event.toContext(jsonAdapter), event.getErrorType()).also {
            logger.debug("Received error event response for {}:{}", it.event.name, it.event.version)
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
