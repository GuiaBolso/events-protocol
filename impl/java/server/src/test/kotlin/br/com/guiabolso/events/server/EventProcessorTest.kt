package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EventProcessorTest {

    private lateinit var rawEventProcessor: EventProcessor
    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry

    @BeforeEach
    fun setUp() {
        eventHandlerRegistry = SimpleEventHandlerRegistry()
        exceptionHandlerRegistry = ExceptionHandlerRegistry()
        rawEventProcessor = EventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)
    }

    @Test
    fun testCanProcessEvent() {
        val event = buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version) {
            buildResponseEvent()
        }

        val responseEvent = rawEventProcessor.processEvent(buildRequestEventString())

        assertEquals(
            "{\"name\":\"event:name:response\",\"version\":1,\"id\":\"id\",\"flowId\":\"flowId\",\"payload\":42,\"identity\":{},\"auth\":{},\"metadata\":{}}",
            responseEvent
        )
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val responseEvent =
            MapperHolder.mapper.fromJson(rawEventProcessor.processEvent("THIS IS NOT A EVENT"), RawEvent::class.java)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload!!.asJsonObject["code"].asString)
    }

    private fun buildRequestEventString(event: RequestEvent = buildRequestEvent()) =
        MapperHolder.mapper.toJson(event)!!
}
