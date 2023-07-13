package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.JsonAdapterProducer
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.events.server.handler.toContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class SimpleEventHandlerRegistryTest {

    @Test
    fun `test can add multiple events (collection)`() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        val handlers = listOf(Handler1, Handler2)

        eventHandlerDiscovery.addAll(handlers)

        val handler1 = eventHandlerDiscovery.eventHandlerFor(Handler1.eventName, Handler1.eventVersion)
        val handler2 = eventHandlerDiscovery.eventHandlerFor(Handler2.eventName, Handler2.eventVersion)
        assertEquals(handler1, Handler1)
        assertEquals(handler2, Handler2)
    }

    @Test
    fun `test can add multiple events (vararg)`() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        val handlers = listOf(Handler1, Handler2)

        eventHandlerDiscovery.addAll(*handlers.toTypedArray())

        val handler1 = eventHandlerDiscovery.eventHandlerFor(Handler1.eventName, Handler1.eventVersion)
        val handler2 = eventHandlerDiscovery.eventHandlerFor(Handler2.eventName, Handler2.eventVersion)
        assertEquals(handler1, Handler1)
        assertEquals(handler2, Handler2)
    }

    @Test
    fun testCanHandleEvent() = runBlocking {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        eventHandlerDiscovery.add("event:name", 1) {
            EventBuilderForTest.buildResponseEvent()
        }

        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)!!
        val eventContext = EventBuilderForTest.buildRequestEvent().toContext(JsonAdapterProducer.mapper)
        val response = handler.handle(eventContext)
        assertEquals(EventBuilderForTest.buildResponseEvent(), response)
    }

    @Test
    fun testReturnsNullWhenEventNotFound() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)

        assertNull(handler)
    }

    @Test
    fun testThrowsExceptionWhenRegisteringDuplicatedEvent() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        val handlers = listOf(Handler1, Handler1)

        assertThrows(IllegalStateException::class.java) {
            eventHandlerDiscovery.addAll(*handlers.toTypedArray())
        }
    }
}

private object Handler1 : EventHandler {
    var handles = 0

    override val eventName = "Dummy1"
    override val eventVersion = 1

    override suspend fun handle(event: RequestEventContext): ResponseEvent {
        handles++
        return event.response { }
    }
}

private object Handler2 : EventHandler {
    var handles = 0

    override val eventName = "Dummy2"
    override val eventVersion = 1

    override suspend fun handle(event: RequestEventContext): ResponseEvent {
        handles++
        return event.response {}
    }
}
