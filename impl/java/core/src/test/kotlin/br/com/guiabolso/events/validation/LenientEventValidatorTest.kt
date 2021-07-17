package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import com.google.gson.JsonNull.INSTANCE as JsonNull

class LenientEventValidatorTest {

    @Suppress("DEPRECATION")
    private val validator = LenientEventValidator()

    @Test
    fun testResponseValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject())

        val response = validator.validateAsResponseEvent(raw)
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
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    null,
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutVersion() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    null,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutId() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    null,
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutPayload() {
        val raw = RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject())

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonNull, response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseValidationWithNullPayload() {
        val raw = RawEvent("event", 1, "id", "flow", JsonNull, JsonObject(), JsonObject(), JsonObject())

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonNull, response.payload)
        assertEquals(JsonObject(), response.auth)
        assertEquals(JsonObject(), response.identity)
        assertEquals(JsonObject(), response.metadata)
    }

    @Test
    fun testResponseValidationWithoutIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject())

        val response = validator.validateAsResponseEvent(raw)
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
    fun testResponseValidationWithoutAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject())

        val response = validator.validateAsResponseEvent(raw)
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
    fun testResponseValidationWithoutMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null)

        val response = validator.validateAsResponseEvent(raw)
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
    fun testRequestValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), JsonObject())

        val request = validator.validateAsRequestEvent(raw)
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
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    null,
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutVersion() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    null,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutId() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    null,
                    "flow",
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutFlowId() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    null,
                    JsonPrimitive(42),
                    JsonObject(),
                    JsonObject(),
                    JsonObject()
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutPayload() {
        val raw = RawEvent("event", 1, "id", "flow", null, JsonObject(), JsonObject(), JsonObject())

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonNull, request.payload)
        assertEquals(JsonObject(), request.auth)
        assertEquals(JsonObject(), request.identity)
        assertEquals(JsonObject(), request.metadata)
    }

    @Test
    fun testRequestValidationWithNullPayload() {
        val raw = RawEvent("event", 1, "id", "flow", JsonNull, JsonObject(), JsonObject(), JsonObject())

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonNull, request.payload)
        assertEquals(JsonObject(), request.auth)
        assertEquals(JsonObject(), request.identity)
        assertEquals(JsonObject(), request.metadata)
    }

    @Test
    fun testRequestValidationWithoutIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, JsonObject(), JsonObject())

        val request = validator.validateAsRequestEvent(raw)
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
    fun testRequestValidationWithoutAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), null, JsonObject())

        val request = validator.validateAsRequestEvent(raw)
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
    fun testRequestValidationWithoutMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonObject(), JsonObject(), null)

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(JsonObject(), request.auth)
        assertEquals(JsonObject(), request.identity)
        assertEquals(JsonObject(), request.metadata)
    }

}
