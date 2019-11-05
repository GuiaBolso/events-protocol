package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.exception.MissingRequiredParameterException
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class SecureTypedEventHandlerTest {

    @Test
    fun `should successfully handle event with valid payload`() {
        val handler = TestSecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonPrimitive("José"), identity = userId(21))

        assertEquals("21:JoséJosé", handler.handle(event).payloadAs())
    }

    @Test
    fun `should fail to handle event with invalid payload`() {
        val handler = TestSecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonNull.INSTANCE, identity = userId(42))

        val ex = assertThrows(MissingRequiredParameterException::class.java) {
            handler.handle(event)
        }

        assertEquals("payload", ex.eventMessage.parameters["name"])
    }

    @Test
    fun `should fail to handle event with invalid userId`() {
        val handler = TestSecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonPrimitive("José"), identity = JsonObject())

        val ex = assertThrows(MissingRequiredParameterException::class.java) {
            handler.handle(event)
        }

        assertEquals("identity.userId", ex.eventMessage.parameters["name"])
    }

    @Test
    fun `should successfully handle event without payload`() {
        val handler = TestUnitSecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonNull.INSTANCE, identity = userId(63))

        assertEquals("63:José", handler.handle(event).payloadAs())
    }

    private fun userId(userId: Long) = JsonObject().apply {
        add("userId", JsonPrimitive(userId))
    }

    private class TestSecureTypedEventHandler : SecureTypedEventHandler<String, String>() {
        override val eventName = "test:event"
        override val eventVersion = 1
        override val inputType = String::class
        override val outputType = String::class

        override fun handle(userId: Long, payload: String): String {
            return "$userId:$payload$payload"
        }
    }

    private class TestUnitSecureTypedEventHandler : SecureTypedEventHandler<Unit, String>() {
        override val eventName = "test:event"
        override val eventVersion = 1
        override val inputType = Unit::class
        override val outputType = String::class

        override fun handle(userId: Long, payload: Unit): String {
            return "$userId:José"
        }
    }

}