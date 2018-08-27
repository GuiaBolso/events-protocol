package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsResponseEvent
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class EventValidatorTest {

    @Test
    fun testResponseValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject())

        val response = validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseValidationWithoutName() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent(null, 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutVersion() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", null, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutId() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", 1, null, "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutPayload() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutIdentity() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutAuth() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject()))
        }
    }

    @Test
    fun testResponseValidationWithoutMetadata() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null))
        }
    }

    @Test
    fun testRequestValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject())

        val request = validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(JsonObject(), request.auth)
        assertEquals(JsonObject(), request.identity)
        assertEquals(JsonObject(), request.metadata)
    }

    @Test
    fun testRequestValidationWithoutName() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent(null, 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutVersion() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", null, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutId() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, null, "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutFlowId() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, "id", null, JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutPayload() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutIdentity() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutAuth() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject()))
        }
    }

    @Test
    fun testRequestValidationWithoutMetadata() {
        assertThrows(IllegalArgumentException::class.java) {
            validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null))
        }
    }

}