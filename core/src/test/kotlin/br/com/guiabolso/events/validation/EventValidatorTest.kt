package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsRequestEvent
import br.com.guiabolso.events.validation.EventValidator.validateAsResponseEvent
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

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

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutName() {
        validateAsResponseEvent(RawEvent(null, 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutVersion() {
        validateAsResponseEvent(RawEvent("event", null, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutId() {
        validateAsResponseEvent(RawEvent("event", 1, null, "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutPayload() {
        validateAsResponseEvent(RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutIdentity() {
        validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutAuth() {
        validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testResponseValidationWithoutMetadata() {
        validateAsResponseEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null))
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

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutName() {
        validateAsRequestEvent(RawEvent(null, 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutVersion() {
        validateAsRequestEvent(RawEvent("event", null, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutId() {
        validateAsRequestEvent(RawEvent("event", 1, null, "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutFlowId() {
        validateAsRequestEvent(RawEvent("event", 1, "id", null, JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutPayload() {
        validateAsRequestEvent(RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutIdentity() {
        validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutAuth() {
        validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequestValidationWithoutMetadata() {
        validateAsRequestEvent(RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null))
    }


}