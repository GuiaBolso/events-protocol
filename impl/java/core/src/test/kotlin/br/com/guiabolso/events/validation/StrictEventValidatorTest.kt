package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StrictEventValidatorTest {

    private val validator = StrictEventValidator()

    @Test
    fun testResponseValidation() {
        val raw = RawEvent(
            "event",
            1,
            "id",
            "flow",
            JsonPrimitive(42),
            buildJsonObject {},
            buildJsonObject {},
            buildJsonObject {}
        )

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
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    null,
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
        validator.validateAsResponseEvent(
            RawEvent(
                "event",
                1,
                "id",
                "flow",
                JsonNull,
                buildJsonObject {},
                buildJsonObject {},
                buildJsonObject {}
            )
        )
    }

    @Test
    fun testResponseValidationWithoutIdentity() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    null,
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonNull,
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutAuth() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    null,
                    buildJsonObject {}
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    JsonNull,
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testResponseValidationWithoutMetadata() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    buildJsonObject {},
                    null
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    buildJsonObject {},
                    JsonNull
                )
            )
        }
    }

    @Test
    fun testRequestValidation() {
        val raw = RawEvent(
            "event",
            1,
            "id",
            "flow",
            JsonPrimitive(42),
            buildJsonObject {},
            buildJsonObject {},
            buildJsonObject { }
        )

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
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    null,
                    buildJsonObject {},
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
        validator.validateAsRequestEvent(
            RawEvent(
                "event",
                1,
                "id",
                "flow",
                JsonNull,
                buildJsonObject {},
                buildJsonObject {},
                buildJsonObject {}
            )
        )
    }

    @Test
    fun testRequestValidationWithoutIdentity() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    null,
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    JsonNull,
                    buildJsonObject {},
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutAuth() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    null,
                    buildJsonObject {}
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    JsonNull,
                    buildJsonObject {}
                )
            )
        }
    }

    @Test
    fun testRequestValidationWithoutMetadata() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    buildJsonObject {},
                    null
                )
            )
        }
        assertThrows(EventValidationException::class.java) {
            validator.validateAsRequestEvent(
                RawEvent(
                    "event",
                    1,
                    "id",
                    "flow",
                    JsonPrimitive(42),
                    buildJsonObject {},
                    buildJsonObject {},
                    JsonNull
                )
            )
        }
    }
}
