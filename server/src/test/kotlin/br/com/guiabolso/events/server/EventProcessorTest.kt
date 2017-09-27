package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerDiscovery
import br.com.guiabolso.events.server.metric.MetricReporter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EventProcessorTest {

    private lateinit var eventProcessor: EventProcessor
    private lateinit var eventHandlerDiscovery: SimpleEventHandlerDiscovery

    @Before
    fun setUp() {
        eventHandlerDiscovery = SimpleEventHandlerDiscovery()

        eventProcessor = EventProcessor(eventHandlerDiscovery)
    }

    @Test
    fun testCanProcessEvent() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerDiscovery.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                return EventBuilderForTest.buildResponseEvent()
            }

        })

        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        assertEquals(EventBuilderForTest.buildResponseEvent(), responseEvent)
    }

    @Test
    fun testEventNotFound() {
        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        val eventMessage = EventMessage("NO_EVENT_HANDLER_FOUND", mapOf("event" to "event:name", "version" to 1))
        val expectedResponse = EventBuilderForTest
                .buildResponseEvent()
                .copy(
                        name = "eventNotFound",
                        payload = mapper.toJsonTree(eventMessage)
                )


        assertEquals(expectedResponse, responseEvent)
    }


    @Test
    fun testEventThrowException() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerDiscovery.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                throw RuntimeException("error")
            }

        })

        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        assertEquals("${event.name}:error", responseEvent.name)
        assertEquals("UNHANDLED_ERROR", responseEvent.payload.asJsonObject["code"].asString)
    }


    @Test
    fun testCanHandleException() {
        val event = EventBuilderForTest.buildRequestEvent()

        eventHandlerDiscovery.add(event.name, event.version, object : EventHandler {

            override fun handle(event: RequestEvent): ResponseEvent {
                throw RuntimeException("error")
            }

        })

        eventProcessor.register(RuntimeException::class.java, object : EventExceptionHandler<RuntimeException> {

            override fun handleException(exception: RuntimeException, event: Event, metricReporter: MetricReporter): ResponseEvent {
                return EventBuilderForTest.buildResponseEvent().copy("${event.name}:bad_request")
            }

        })

        val responseEvent = eventProcessor.processEvent(EventBuilderForTest.buildRequestEventString())

        assertEquals("${event.name}:bad_request", responseEvent.name)
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val responseEvent = eventProcessor.processEvent("THIS IS NOT A EVENT")

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.asJsonObject["code"].asString)
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

        val responseEvent = eventProcessor.processEvent(eventWithoutVersion)

        assertEquals("badProtocol", responseEvent.name)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", responseEvent.payload.asJsonObject["code"].asString)
        assertEquals("version", responseEvent.payload.asJsonObject["parameters"].asJsonObject["missingProperty"].asString)
    }

}