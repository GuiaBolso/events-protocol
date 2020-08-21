package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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

        val input = MapperHolder.mapper.encodeToString(event).asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val responseEvent = MapperHolder.mapper.decodeFromString<RawEvent>(String(output.toByteArray()))

        assertEquals("event:name:response", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals(42, responseEvent.payload?.jsonPrimitive?.int)
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val input = "THIS IS NOT A EVENT".asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val responseEvent = MapperHolder.mapper.decodeFromString<RawEvent>(String(output.toByteArray()))

        assertEquals("badProtocol", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals(
            "INVALID_COMMUNICATION_PROTOCOL",
            responseEvent.payload!!.jsonObject["code"]?.jsonPrimitive?.content
        )
    }

    private fun String.asStream() = ByteArrayInputStream(this.toByteArray())
}
