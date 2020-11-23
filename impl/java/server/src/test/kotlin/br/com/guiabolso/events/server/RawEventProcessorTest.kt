package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRawRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RawEventProcessorTest {

    private val eventHandlerRegistry = SimpleEventHandlerRegistry()
    private val exceptionHandlerRegistry = ExceptionHandlerRegistry()
    private val rawEventProcessor = RawEventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)

    @Test
    fun testCanProcessEvent() = runBlocking {
        val event = buildRawRequestEvent()
        val expectedResponse = buildResponseEvent()

        eventHandlerRegistry.add(event.name!!, event.version!!) {
            expectedResponse
        }

        val responseEvent = rawEventProcessor.processEvent(event)

        assertEquals(expectedResponse, responseEvent)
    }

    @Test
    fun testEventNotFound() = runBlocking {
        val responseEvent = rawEventProcessor.processEvent(buildRawRequestEvent())

        assertEquals("id", responseEvent.id)
        assertEquals("flowId", responseEvent.flowId)
        assertEquals("eventNotFound", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals("NO_EVENT_HANDLER_FOUND", responseEvent.payload.asJsonObject.get("code").asString)
        val parameters = responseEvent.payload.asJsonObject.get("parameters").asJsonObject
        assertEquals("event:name", parameters.get("event").asString)
        assertEquals(1, parameters.get("version").asInt)
    }

    @Test
    fun testEventThrowException() = runBlocking {
        val event = buildRawRequestEvent()

        eventHandlerRegistry.add(event.name!!, event.version!!) {
            throw RuntimeException("error")
        }

        val responseEvent = rawEventProcessor.processEvent(buildRawRequestEvent())

        assertEquals("${event.name}:error", responseEvent.name)
        assertEquals("UNHANDLED_ERROR", responseEvent.payload.asJsonObject["code"].asString)
    }

    @Test
    fun testCanHandleException() = runBlocking {
        val event = buildRawRequestEvent()

        eventHandlerRegistry.add(event.name!!, event.version!!) {
            throw RuntimeException("error")
        }

        exceptionHandlerRegistry.register(RuntimeException::class.java) { _, requestEvent, _ ->
            buildResponseEvent().copy("${requestEvent.name}:bad_request")
        }

        val responseEvent = rawEventProcessor.processEvent(event)

        assertEquals("${event.name}:bad_request", responseEvent.name)
    }

    @Test
    fun testBadProtocolEventIsReturned() = runBlocking {
        val responseEvent = rawEventProcessor.processEvent(null)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.asJsonObject["code"].asString)
    }

    @Test
    fun testBadProtocolEventIsReturnedWhenParameterIsMissing() = runBlocking {
        val event = buildRawRequestEvent().copy(version = null)

        val responseEvent = rawEventProcessor.processEvent(event)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.asJsonObject["code"].asString)
        assertEquals(
            "version",
            responseEvent.payload.asJsonObject["parameters"].asJsonObject["missingProperty"].asString
        )
    }
}
