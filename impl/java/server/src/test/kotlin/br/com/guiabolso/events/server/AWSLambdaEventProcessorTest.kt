package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class AWSLambdaEventProcessorTest {

    private lateinit var lambdaEventProcessor: AWSLambdaEventProcessor
    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry

    @BeforeEach
    fun setUp() {
        eventHandlerRegistry = SimpleEventHandlerRegistry()
        exceptionHandlerRegistry = ExceptionHandlerRegistry()
        lambdaEventProcessor = AWSLambdaEventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)
    }

    @Test
    fun testCanProcessEvent() {
        val event = buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version) {
            buildResponseEvent()
        }

        val input = MapperHolder.mapper.toJson(event).asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val responseEvent = MapperHolder.mapper.fromJson(String(output.toByteArray()), RawEvent::class.java)

        assertEquals("event:name:response", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals(42, responseEvent.payload?.asInt)
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val input = "THIS IS NOT A EVENT".asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val responseEvent = MapperHolder.mapper.fromJson(String(output.toByteArray()), RawEvent::class.java)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload!!.asJsonObject.get("code").asString)
    }

    private fun String.asStream() = ByteArrayInputStream(this.toByteArray())
}
