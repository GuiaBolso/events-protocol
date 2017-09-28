package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerDiscovery
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SimpleEventHandlerDiscoveryTest {

    @Test
    fun testCanHandleEvent() {
        val eventHandlerDiscovery = SimpleEventHandlerDiscovery()

        eventHandlerDiscovery.add("event:name", 1, object : EventHandler {
            override fun handle(event: RequestEvent): ResponseEvent {
                return EventBuilderForTest.buildResponseEvent()
            }
        })


        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)!!

        val responseEvent = handler.handle(EventBuilderForTest.buildRequestEvent())

        assertEquals(EventBuilderForTest.buildResponseEvent(), responseEvent)
    }


    @Test
    fun testReturnsNullWhenEventNotFound() {
        val eventHandlerDiscovery = SimpleEventHandlerDiscovery()

        val handler = eventHandlerDiscovery.eventHandlerFor("event:name", 1)

        assertNull(handler)
    }
}

