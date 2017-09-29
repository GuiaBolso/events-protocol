package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SimpleEventHandlerRegistryTest {

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

