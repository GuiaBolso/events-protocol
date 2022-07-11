package br.com.guiabolso.events.validation

import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.model.RawEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class StrictEventValidatorTest {

    private val validator = StrictEventValidator()

    @Test
    fun testResponseValidation() {
        val raw = RawEvent("event", 1, "id", "flow", PrimitiveNode(42), TreeNode(), TreeNode(), TreeNode())

        val response = validator.validateAsResponseEvent(raw)
        assertEquals("event", response.name)
        assertEquals(1, response.version)
        assertEquals("id", response.id)
        assertEquals("flow", response.flowId)
        assertEquals(PrimitiveNode(42), response.payload)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                TreeNode(),
                TreeNode(),
                TreeNode()
            )
        )
    }

    @Test
    fun testResponseValidationWithoutIdentity() {
        assertThrows(EventValidationException::class.java) {
            validator.validateAsResponseEvent(
                RawEvent(
                    name = "event",
                    version = 1,
                    id = "id",
                    flowId = "flow",
                    payload = PrimitiveNode(42),
                    identity = null,
                    auth = TreeNode(),
                    metadata = TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    null,
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    null
                )
            )
        }
    }

    @Test
    fun testRequestValidation() {
        val raw = RawEvent("event", 1, "id", "flow", PrimitiveNode(42), TreeNode(), TreeNode(), TreeNode())

        val request = validator.validateAsRequestEvent(raw)
        assertEquals("event", request.name)
        assertEquals(1, request.version)
        assertEquals("id", request.id)
        assertEquals("flow", request.flowId)
        assertEquals(PrimitiveNode(42), request.payload)
        assertEquals(TreeNode(), request.auth)
        assertEquals(TreeNode(), request.identity)
        assertEquals(TreeNode(), request.metadata)
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                    TreeNode(),
                    TreeNode(),
                    TreeNode()
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
                TreeNode(),
                TreeNode(),
                TreeNode()
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
                    PrimitiveNode(42),
                    null,
                    TreeNode(),
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    null,
                    TreeNode()
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
                    PrimitiveNode(42),
                    TreeNode(),
                    TreeNode(),
                    null
                )
            )
        }
    }
}
