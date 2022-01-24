package br.com.guiabolso.events.utils

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.EventErrorType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventsTest {

    @Test
    fun testIsSuccess() {
        val responseEvent = buildResponseEvent()
        assertTrue(responseEvent.isSuccess())

        val responseEvent2 = buildResponseEvent().copy(name = "event:name:error")
        assertFalse(responseEvent2.isSuccess())
    }

    @Test
    fun testIsError() {
        val responseEvent = buildResponseEvent()
        assertFalse(responseEvent.isError())

        val responseEvent2 = buildResponseEvent().copy(name = "event:name:error")
        assertTrue(responseEvent2.isError())
    }

    @Test
    fun testGetErrorType() {
        val responseEvent = buildResponseEvent().copy(name = "event:name:error")
        assertTrue(responseEvent.getErrorType() is EventErrorType.Generic)
        assertTrue(responseEvent.getErrorType() == EventErrorType.Generic)
    }

    @Test
    fun testGetErrorTypeOnSuccess() {
        assertThrows(IllegalStateException::class.java) {
            val responseEvent = buildResponseEvent()
            responseEvent.getErrorType()
        }
    }

    @Test
    fun testGetUserIdAsStringFromIdentityWhenItIsNull() {
        val event = buildRequestEvent()
        assertNull(event.userIdAsString)
    }

    @Test
    fun testGetUserIdAsStringFromIdentityWhenItIsNumber() {
        val event = buildRequestEvent().copy(
            identity = TreeNode("userId" to JsonLiteral(42))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdAsStringFromIdentityWhenItIsString() {
        val event = buildRequestEvent().copy(
            identity = TreeNode("userId" to JsonLiteral("42"))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdFromIdentity() {
        assertNull(buildRequestEvent().userId)

        val responseEvent = buildRequestEvent().copy(
            identity = TreeNode("userId" to JsonLiteral(42))
        )
        assertEquals(42L, responseEvent.userId)
    }

    @Test
    fun testGetOriginFromMetadata() {
        assertNull(buildRequestEvent().origin)

        val responseEvent = buildRequestEvent().copy(
            metadata = TreeNode("origin" to JsonLiteral("batata"))
        )
        assertEquals("batata", responseEvent.origin)
    }

    @Test
    fun testGetPayload() {
        val request = buildRequestEvent().copy(
            payload = TreeNode("a" to JsonLiteral("someString"), "b" to JsonLiteral(60))
        )

        val vo = request.payloadAs(VO::class.java)
        val vo2: VO = request.payloadAs()

        assertEquals("someString", vo.a)
        assertEquals(60L, vo.b)

        assertEquals("someString", vo2.a)
        assertEquals(60L, vo2.b)
    }

    @Test
    fun testGetIdentity() {
        val request = buildRequestEvent().copy(
            identity = TreeNode(
                "a" to JsonLiteral("someString"),
                "b" to JsonLiteral(60)
            )
        )

        val vo = request.identityAs(VO::class.java)
        val vo2: VO = request.identityAs()

        assertEquals("someString", vo.a)
        assertEquals(60L, vo.b)

        assertEquals("someString", vo2.a)
        assertEquals(60L, vo2.b)
    }

    @Test
    fun testGetAuth() {
        val request = buildRequestEvent().copy(
            auth = TreeNode(
                "a" to JsonLiteral("someString"),
                "b" to JsonLiteral(60)
            )
        )

        val vo = request.authAs(VO::class.java)
        val vo2: VO = request.authAs()

        assertEquals("someString", vo.a)
        assertEquals(60L, vo.b)

        assertEquals("someString", vo2.a)
        assertEquals(60L, vo2.b)
    }

    @Test
    fun testCanParseJsonArrays() {
        val request = buildRequestEvent().copy(
            payload = ArrayNode().apply {
                this.add(
                    TreeNode(
                        "a" to JsonLiteral("someString"),
                        "b" to JsonLiteral(60)
                    )
                )
                this.add(
                    TreeNode(
                        "a" to JsonLiteral("someOtherString"),
                        "b" to JsonLiteral(120)
                    )
                )
            }
        )

        val voList: List<VO> = request.payloadAs()

        assertEquals("someString", voList[0].a)
        assertEquals(60L, voList[0].b)

        assertEquals("someOtherString", voList[1].a)
        assertEquals(120L, voList[1].b)
    }

    private data class VO(val a: String? = null, val b: Long? = null)
}
