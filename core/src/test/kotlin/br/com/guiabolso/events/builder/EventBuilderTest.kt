package br.com.guiabolso.events.builder

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.event
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseEvent
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.events.exception.MissingEventInformationException
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.utils.EventUtils
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
        val event = EventUtils.withContext(EventContext("id", "flowId")) {
            event {
                name = "event"
                version = 1
                payload = 42
            }
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
    fun testCreateEventWithIdAndFlowIdForwardOverwritten() {
        val event = EventUtils.withContext(EventContext("id", "flowId")) {
            event {
                name = "event"
                id = "otherId"
                flowId = "otherFlowId"
                version = 1
                payload = 42
            }
        }

        assertEquals("otherId", event.id)
        assertEquals("otherFlowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertEquals(JsonPrimitive(42), event.payload)
        assertEquals(JsonObject(), event.auth)
        assertEquals(JsonObject(), event.identity)
        assertEquals(JsonObject(), event.metadata)
    }

    @Test
    fun testCreateEventWithoutIdAndFlowId() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                name = "event"
                version = 1
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutName() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                version = 1
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutVersion() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                name = "event"
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutPayload() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                name = "event"
            }
        }
    }

    @Test
    fun testResponseEvent() {
        val response = responseEvent {
            id = "id"
            flowId = "flowId"
            name = "event:response"
            version = 1
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertEquals(JsonPrimitive(84), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseForEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = responseFor(event) {
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertEquals(JsonPrimitive(84), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseForEventWithIdAndFlowIdForward() {
        val event = EventUtils.withContext(EventContext("id", "flowId")) {
            event {
                name = "event"
                version = 1
                payload = 42
            }
        }
        val response = responseFor(event) {
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertEquals(JsonPrimitive(84), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)

        EventContextHolder.clean()
    }

    @Test
    fun testResponseForEventWithIdAndFlowIdForwardOverwritten() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = responseFor(event) {
            id = "otherId"
            flowId = "otherFlowId"
            payload = 84
        }

        assertEquals("otherId", response.id)
        assertEquals("otherFlowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertEquals(JsonPrimitive(84), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseForEventWithoutIdAndFlowId() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            responseFor(event) {
                id = null
                flowId = null
                payload = 84
            }
        }
    }

    @Test
    fun testResponseForEventWithoutName() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            responseFor(event) {
                name = null
                payload = 84
            }
        }
    }

    @Test
    fun testResponseForEventWithoutVersion() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            responseFor(event) {
                version = null
                payload = 84
            }
        }
    }

    @Test
    fun testResponseForEventWithoutPayload() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            responseFor(event) {
            }
        }
    }

    @Test
    fun testCreateErrorResponseEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = errorFor(event, EventErrorType.Generic, EventMessage("code", emptyMap()))

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:error", response.name)
        assertEquals(1, response.version)
        assertEquals(MapperHolder.mapper.toJsonTree(EventMessage("code", emptyMap())), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testCreateEventNotFoundResponseEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = eventNotFound(event)

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("eventNotFound", response.name)
        assertEquals(1, response.version)
        assertEquals(MapperHolder.mapper.toJsonTree(EventMessage("NO_EVENT_HANDLER_FOUND", mapOf("event" to event.name, "version" to event.version))), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testCreateBadProtocolResponseEvent() {
        val response = badProtocol(EventMessage("INVALID_COMMUNICATION_PROTOCOL", emptyMap()))

        assertNotNull(response.id)
        assertNotNull(response.flowId)
        assertEquals("badProtocol", response.name)
        assertEquals(1, response.version)
        assertEquals(MapperHolder.mapper.toJsonTree(EventMessage("INVALID_COMMUNICATION_PROTOCOL", emptyMap())), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

}