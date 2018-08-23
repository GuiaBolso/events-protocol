package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.metrics.MetricReporter
import com.nhaarman.mockito_kotlin.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventProcessorTest {

    private lateinit var eventProcessor: EventProcessor
    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry
    private lateinit var reporter: MetricReporter

    @Before
    fun setUp() {
        eventHandlerRegistry = SimpleEventHandlerRegistry()
        exceptionHandlerRegistry = ExceptionHandlerRegistry()
        reporter = mock()
        eventProcessor = EventProcessor(eventHandlerRegistry)
    }

    @Test
    fun testCanProcessEvent() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                return EventBuilderForTest.buildResponseEvent()
            }

        })

        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        assertEquals("{\"name\":\"event:name:response\",\"version\":1,\"id\":\"id\",\"flowId\":\"flowId\",\"payload\":42,\"identity\":{},\"auth\":{},\"metadata\":{}}", responseEvent)
    }

    @Test
    fun testEventNotFound() {
        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        assertEquals("{\"name\":\"eventNotFound\",\"version\":1,\"id\":\"id\",\"flowId\":\"flowId\",\"payload\":{\"code\":\"NO_EVENT_HANDLER_FOUND\",\"parameters\":{\"event\":\"event:name\",\"version\":1}},\"identity\":{},\"auth\":{},\"metadata\":{}}", responseEvent)
    }

    @Test
    fun testEventThrowException() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                throw RuntimeException("error")
            }

        })

        val responseEvent = MapperHolder.mapper.fromJson(eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString()), RawEvent::class.java)

        assertEquals("${event.name}:error", responseEvent.name)
        assertEquals("UNHANDLED_ERROR", responseEvent.payload!!.asJsonObject["code"].asString)
    }

    @Test
    fun testCanHandleException() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                throw RuntimeException("error")
            }

        })

        eventProcessor.register(RuntimeException::class.java) { _, requestEvent, _ ->
            EventBuilderForTest.buildResponseEvent().copy("${requestEvent.name}:bad_request")
        }

        val responseEvent = MapperHolder.mapper.fromJson(eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString()), RawEvent::class.java)

        assertEquals("${event.name}:bad_request", responseEvent.name)
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val responseEvent = MapperHolder.mapper.fromJson(eventProcessor.processEvent("THIS IS NOT A EVENT"), RawEvent::class.java)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload!!.asJsonObject["code"].asString)
    }

    @Test
    fun testBadProtocolEventIsReturnedWhenParameterIsMissing() {

        val eventWithoutVersion = """
            {
              "name": "event:name",
              "id": "sjfid",
              "flowId": "dsókf0sd",
              "payload":{},
              "metadata": {}
            }
        """.trimIndent()

        val responseEvent = MapperHolder.mapper.fromJson(eventProcessor.processEvent(eventWithoutVersion), RawEvent::class.java)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload!!.asJsonObject["code"].asString)
        assertEquals("version", responseEvent.payload!!.asJsonObject["parameters"].asJsonObject["missingProperty"].asString)
    }

    @Test(expected = Exception::class)
    fun testProcessEventWithThrowException() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                throw Exception("Test throw exception")
            }

        })
        val eventProcessor = EventProcessor(eventHandlerRegistry, exceptionHandlerRegistry, reporter, true)

        eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())
    }

}