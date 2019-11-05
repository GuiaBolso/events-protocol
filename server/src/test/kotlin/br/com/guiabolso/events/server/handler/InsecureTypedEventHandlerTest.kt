package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.exception.MissingRequiredParameterException
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InsecureTypedEventHandlerTest {

    @Test
    fun `should successfully handle event with valid payload`() {
        val handler = TestInsecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonPrimitive("José"))

        assertEquals("JoséJosé", handler.handle(event).payloadAs())
    }

    @Test
    fun `should fail to handle event with invalid payload`() {
        val handler = TestInsecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonNull.INSTANCE)

        val ex = Assertions.assertThrows(MissingRequiredParameterException::class.java) {
            handler.handle(event)
        }

        assertEquals("payload", ex.eventMessage.parameters["name"])
    }

    @Test
    fun `should successfully handle event without payload`() {
        val handler = TestUnitInsecureTypedEventHandler()
        val event = buildRequestEvent().copy(payload = JsonNull.INSTANCE)

        assertEquals("José", handler.handle(event).payloadAs())
    }

    private class TestInsecureTypedEventHandler : InsecureTypedEventHandler<String, String>() {
        override val eventName = "test:event"
        override val eventVersion = 1
        override val inputType = String::class
        override val outputType = String::class

        override fun handle(payload: String): String {
            return "$payload$payload"
        }
    }

    private class TestUnitInsecureTypedEventHandler : InsecureTypedEventHandler<Unit, String>() {
        override val eventName = "test:event"
        override val eventVersion = 1
        override val inputType = Unit::class
        override val outputType = String::class

        override fun handle(payload: Unit): String {
            return "José"
        }
    }

}