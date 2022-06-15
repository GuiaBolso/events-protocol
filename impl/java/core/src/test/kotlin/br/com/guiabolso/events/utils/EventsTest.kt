package br.com.guiabolso.events.utils

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.User
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
            identity = TreeNode("userId" to PrimitiveNode(42))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdWithObjectAsStringFromIdentityWhenItIsNumber() {
        val event = buildRequestEvent().copy(
            identity = TreeNode("user" to TreeNode("id" to PrimitiveNode(42)))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdAsStringFromIdentityWhenItIsString() {
        val event = buildRequestEvent().copy(
            identity = TreeNode("userId" to PrimitiveNode("42"))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdWithObjectAsStringFromIdentityWhenItIsString() {
        val event = buildRequestEvent().copy(
            identity = TreeNode("user" to TreeNode("id" to PrimitiveNode("42")))
        )
        assertEquals("42", event.userIdAsString)
    }

    @Test
    fun testGetUserIdFromIdentity() {
        assertNull(buildRequestEvent().userId)

        val responseEvent = buildRequestEvent().copy(
            identity = TreeNode("userId" to PrimitiveNode(42))
        )
        assertEquals(42L, responseEvent.userId)
    }

    @Test
    fun testGetUserIdWithObjectFromIdentity() {
        assertNull(buildRequestEvent().userId)

        val responseEvent = buildRequestEvent().copy(
            identity = TreeNode(
                "user" to TreeNode("id" to PrimitiveNode(42))
            )
        )
        assertEquals(42L, responseEvent.userId)
    }

    @Test
    fun testGetUserFromIdentity() {
        val userId = 42L
        val userType = "CONSUMER"

        assertNull(buildRequestEvent().userId)

        val responseEvent = buildRequestEvent().copy(
            identity = TreeNode(
                "user" to TreeNode(
                    "id" to PrimitiveNode(userId),
                    "type" to PrimitiveNode(userType)
                )
            )
        )

        assertEquals(User(id = userId, type = userType), responseEvent.user)
    }

    @Test
    fun testGetUserFromIdentityWhenIsNull() {
        val responseEvent = buildRequestEvent()
        assertNull(responseEvent.user)
    }

    @Test
    fun testGetOriginFromMetadata() {
        assertNull(buildRequestEvent().origin)

        val responseEvent = buildRequestEvent().copy(
            metadata = TreeNode("origin" to PrimitiveNode("batata"))
        )
        assertEquals("batata", responseEvent.origin)
    }

    @Test
    fun testGetPayload() {
        val request = buildRequestEvent().copy(
            payload = TreeNode("type" to PrimitiveNode("someString"), "id" to PrimitiveNode(60))
        )

        val user = request.payloadAs(User::class.java)
        val user2: User = request.payloadAs()

        assertEquals(user, user2)
        assertEquals("someString", user.type)
        assertEquals(60L, user.id)
    }

    @Test
    fun testGetIdentity() {
        val request = buildRequestEvent().copy(
            identity = TreeNode(
                "type" to PrimitiveNode("someString"),
                "id" to PrimitiveNode(60)
            )
        )

        val user = request.identityAs(User::class.java)
        val user2: User = request.identityAs()

        assertEquals(user, user2)
        assertEquals("someString", user.type)
        assertEquals(60L, user.id)
    }

    @Test
    fun testGetAuth() {
        val request = buildRequestEvent().copy(
            auth = TreeNode(
                "type" to PrimitiveNode("someString"),
                "id" to PrimitiveNode(60)
            )
        )

        val user = request.authAs(User::class.java)
        val user2: User = request.authAs()

        assertEquals(user, user2)
        assertEquals("someString", user.type)
        assertEquals(60L, user.id)
    }

    @Test
    fun testCanParseJsonArrays() {
        val request = buildRequestEvent().copy(
            payload = ArrayNode().apply {
                this.add(
                    TreeNode(
                        "type" to PrimitiveNode("someString"),
                        "id" to PrimitiveNode(60)
                    )
                )
                this.add(
                    TreeNode(
                        "type" to PrimitiveNode("someOtherString"),
                        "id" to PrimitiveNode(120)
                    )
                )
            }
        )

        val userList: List<User> = request.payloadAs()

        assertEquals("someString", userList[0].type)
        assertEquals(60L, userList[0].id)

        assertEquals("someOtherString", userList[1].type)
        assertEquals(120L, userList[1].id)
    }
}
