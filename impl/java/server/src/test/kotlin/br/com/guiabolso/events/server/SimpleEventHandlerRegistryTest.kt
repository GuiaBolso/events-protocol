package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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
    fun testCanHandleEvent() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        eventHandlerDiscovery.add("event:name", 1) {
            EventBuilderForTest.buildResponseEvent()
        }

        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)!!

        val responseEvent = handler.handle(EventBuilderForTest.buildRequestEvent())
        assertEquals(EventBuilderForTest.buildResponseEvent(), responseEvent)
    }

    @Test
    fun testReturnsNullWhenEventNotFound() {
        val eventHandlerDiscovery = SimpleEventHandlerRegistry()

        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)

        assertNull(handler)
    }
}

private object Handler1 : EventHandler {
    var handles = 0

    override val eventName = "Dummy1"
    override val eventVersion = 1

    override fun handle(event: RequestEvent): ResponseEvent {
        handles++
        return EventBuilder.responseFor(event) { }
    }
}
private object Handler2 : EventHandler {
    var handles = 0

    override val eventName = "Dummy2"
    override val eventVersion = 1

    override fun handle(event: RequestEvent): ResponseEvent {
        handles++
        return EventBuilder.responseFor(event) { }
    }
}
