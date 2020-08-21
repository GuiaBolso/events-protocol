package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRawRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RawEventProcessorTest {

    private val eventHandlerRegistry = SimpleEventHandlerRegistry()
    private val exceptionHandlerRegistry = ExceptionHandlerRegistry()
    private val rawEventProcessor = RawEventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)

    @Test
    fun testCanProcessEvent() {
        val event = buildRawRequestEvent()
        val expectedResponse = buildResponseEvent()

        eventHandlerRegistry.add(event.name!!, event.version!!) {
            expectedResponse
        }

        val responseEvent = rawEventProcessor.processEvent(event)

        assertEquals(expectedResponse, responseEvent)
    }

    @Test
    fun testEventNotFound() {
        val responseEvent = rawEventProcessor.processEvent(buildRawRequestEvent())

        assertEquals("id", responseEvent.id)
        assertEquals("flowId", responseEvent.flowId)
        assertEquals("eventNotFound", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals("NO_EVENT_HANDLER_FOUND", responseEvent.payload.jsonObject["code"]!!.jsonPrimitive.content)
        val parameters = responseEvent.payload.jsonObject["parameters"]!!.jsonObject
        assertEquals("event:name", parameters["event"]!!.jsonPrimitive.content)
        assertEquals(1, parameters["version"]!!.jsonPrimitive.int)
    }

    @Test
    fun testEventThrowException() {
        val event = buildRawRequestEvent()

        eventHandlerRegistry.add(event.name!!, event.version!!) {
            throw RuntimeException("error")
        }

        val responseEvent = rawEventProcessor.processEvent(buildRawRequestEvent())

        assertEquals("${event.name}:error", responseEvent.name)
        assertEquals("UNHANDLED_ERROR", responseEvent.payload.jsonObject["code"]!!.jsonPrimitive.content)
    }

    @Test
    fun testCanHandleException() {
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
    fun testBadProtocolEventIsReturned() {
        val responseEvent = rawEventProcessor.processEvent(null)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.jsonObject["code"]!!.jsonPrimitive.content)
    }

    @Test
    fun testBadProtocolEventIsReturnedWhenParameterIsMissing() {
        val event = buildRawRequestEvent().copy(version = null)

        val responseEvent = rawEventProcessor.processEvent(event)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.jsonObject["code"]!!.jsonPrimitive.content)
        assertEquals(
            "version",
            responseEvent.payload.jsonObject["parameters"]!!.jsonObject["missingProperty"]!!.jsonPrimitive.content
        )
    }
}
