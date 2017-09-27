package br.com.guiabolso.events.builder

import br.com.guiabolso.events.builder.EventBuilder.Companion.event
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.context.EventContextHolder.setContext
import br.com.guiabolso.events.exception.MissingEventInformationException
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class EventBuilderTest {

    @Test
    fun testCreateEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }

        assertEquals("id", event.id)
        assertEquals("flowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertEquals(JsonPrimitive(42), event.payload)
        assertEquals(JsonObject(), event.auth)
        assertEquals(JsonObject(), event.identity)
        assertEquals(JsonObject(), event.metadata)
    }

    @Test
    fun testCreateEventWithIdAndFlowIdForward() {
        setContext(EventContext("id", "flowId"))

        val event = event {
            name = "event"
            version = 1
            payload = 42
        }

        assertEquals("id", event.id)
        assertEquals("flowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertEquals(JsonPrimitive(42), event.payload)
        assertEquals(JsonObject(), event.auth)
        assertEquals(JsonObject(), event.identity)
        assertEquals(JsonObject(), event.metadata)

        EventContextHolder.clean()
    }

    @Test
    fun testCreateEventWithIdAndFlowIdForwardOverwritten() {
        setContext(EventContext("id", "flowId"))

        val event = event {
            name = "event"
            id = "otherId"
            flowId = "otherFlowId"
            version = 1
            payload = 42
        }

        assertEquals("otherId", event.id)
        assertEquals("otherFlowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertEquals(JsonPrimitive(42), event.payload)
        assertEquals(JsonObject(), event.auth)
        assertEquals(JsonObject(), event.identity)
        assertEquals(JsonObject(), event.metadata)

        EventContextHolder.clean()
    }

    @Test(expected = MissingEventInformationException::class)
    fun testCreateEventWithoutIdAndFlowId() {
        event {
            name = "event"
            version = 1
            payload = 42
        }
    }

    @Test(expected = MissingEventInformationException::class)
    fun testCreateWithoutName() {
        event {
            id = "id"
            flowId = "flowId"
            version = 1
            payload = 42
        }

    }

    @Test(expected = MissingEventInformationException::class)
    fun testCreateWithoutVersion() {
        event {
            id = "id"
            flowId = "flowId"
            name = "event"
            payload = 42
        }

    }

    @Test(expected = MissingEventInformationException::class)
    fun testCreateWithoutPayload() {
        event {
            id = "id"
            flowId = "flowId"
            name = "event"
        }
    }


}