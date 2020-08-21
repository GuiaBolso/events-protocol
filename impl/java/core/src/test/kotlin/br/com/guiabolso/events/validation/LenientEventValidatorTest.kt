package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class LenientEventValidatorTest {

    @Suppress("DEPRECATION")
    private val validator = LenientEventValidator()

    @Test
    fun testResponseValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutPayload() {
        val raw = RawEvent("event", 1, "id", "flow", null, buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonNull, response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithNullPayload() {
        val raw = RawEvent("event", 1, "id", "flow", JsonNull, buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonNull, response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithoutIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, buildJsonObject {}, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithNullIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonNull, buildJsonObject {}, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithoutAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, null, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithNullAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, JsonNull, buildJsonObject {})

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithoutMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, null)

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testResponseValidationWithNullMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, JsonNull)

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(JsonPrimitive(42), response.payload)
        assertEquals(buildJsonObject {}, response.auth)
        assertEquals(buildJsonObject {}, response.identity)
        assertEquals(buildJsonObject {}, response.metadata)
    }

    @Test
    fun testRequestValidation() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
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
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutPayload() {
        val raw = RawEvent("event", 1, "id", "flow", null, buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonNull, request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithNullPayload() {
        val raw = RawEvent("event", 1, "id", "flow", JsonNull, buildJsonObject {}, buildJsonObject {}, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonNull, request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithoutIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), null, buildJsonObject {}, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithNullIdentity() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), JsonNull, buildJsonObject {}, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithoutAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, null, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithNullAuth() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, JsonNull, buildJsonObject {})

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithoutMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, null)

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }

    @Test
    fun testRequestValidationWithNullMetadata() {
        val raw = RawEvent("event", 1, "id", "flow", JsonPrimitive(42), buildJsonObject {}, buildJsonObject {}, JsonNull)

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(JsonPrimitive(42), request.payload)
        assertEquals(buildJsonObject {}, request.auth)
        assertEquals(buildJsonObject {}, request.identity)
        assertEquals(buildJsonObject {}, request.metadata)
    }
}
