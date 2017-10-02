package br.com.guiabolso.events.utils

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.model.EventErrorType
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.*
import org.junit.Test
import java.lang.IllegalStateException

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

    @Test(expected = IllegalStateException::class)
    fun testGetErrorTypeOnSuccess() {
        val responseEvent = buildResponseEvent()
        responseEvent.getErrorType()
    }

    @Test
    fun testGetUserIdFromIdentity() {
        assertNull(buildRequestEvent().userId)

        val responseEvent = buildRequestEvent().copy(
                identity = JsonObject().apply { this.add("userId", JsonPrimitive(42)) }
        )
        assertEquals(42L, responseEvent.userId)
    }

    @Test
    fun testGetPayload() {
        val request = buildRequestEvent().copy(payload = JsonObject().apply {
            this.add("a", JsonPrimitive("someString"))
            this.add("b", JsonPrimitive(60))
        })

        val vo = request.payloadAs(VO::class.java)

        assertEquals("someString", vo.a)
        assertEquals(60L, vo.b)
    }

    private data class VO(val a: String? = null, val b: Long? = null)

}