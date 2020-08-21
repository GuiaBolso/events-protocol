package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SAMLambdaEventProcessorTest {

    private lateinit var lambdaEventProcessor: SAMLambdaEventProcessor
    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry

    @BeforeEach
    fun setUp() {
        eventHandlerRegistry = SimpleEventHandlerRegistry()
        exceptionHandlerRegistry = ExceptionHandlerRegistry()
        lambdaEventProcessor = SAMLambdaEventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)
    }

    @Test
    fun testCanProcessEvent() {
        val event = buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version) {
            buildResponseEvent()
        }

        val input = buildLambdaRequestEventString().asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val lambdaBody =
            mapper.parseToJsonElement(String(output.toByteArray())).jsonObject["body"]!!.jsonPrimitive.content
        val responseEvent = mapper.decodeFromString<RawEvent>(lambdaBody)

        assertEquals("event:name:response", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals(42, responseEvent.payload?.jsonPrimitive?.int)
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val input = "{\"body\":\"THIS IS NOT A EVENT\"}".asStream()
        val output = ByteArrayOutputStream()
        lambdaEventProcessor.processEvent(input, output)

        val lambdaBody =
            mapper.parseToJsonElement(String(output.toByteArray())).jsonObject["body"]!!.jsonPrimitive.content
        val responseEvent = mapper.decodeFromString<RawEvent>(lambdaBody)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals(1, responseEvent.version)
        assertEquals(
            "INVALID_COMMUNICATION_PROTOCOL",
            responseEvent.payload!!.jsonObject["code"]!!.jsonPrimitive.content
        )
    }

    private fun buildLambdaRequestEventString(event: RequestEvent = buildRequestEvent()) = mapper.encodeToString(
        buildJsonObject {
            put("body", JsonPrimitive(mapper.encodeToString(event)))
        }
    )

    private fun String.asStream() = ByteArrayInputStream(this.toByteArray())
}
