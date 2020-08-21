package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.server.exception.BypassedException
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.bypassExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.exceptionHandler
import io.mockk.mockk
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ExceptionHandlerRegistryTest {

    @Test
    fun testCanRegisterExceptionHandler() {
        val exceptionHandlerRegistry = exceptionHandler()

        exceptionHandlerRegistry.register(RuntimeException::class.java) { exception, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive(exception.message))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("Some error", response.payload.jsonPrimitive.content)
    }

    @Test
    fun testExceptionPriority() {
        val exceptionHandlerRegistry = exceptionHandler()

        exceptionHandlerRegistry.register(Exception::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive("Exception"))
        }

        exceptionHandlerRegistry.register(RuntimeException::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive("RuntimeException"))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("Exception", response.payload.jsonPrimitive.content)
    }

    @Test
    fun testExceptionPriority2() {
        val exceptionHandlerRegistry = exceptionHandler()

        exceptionHandlerRegistry.register(RuntimeException::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive("RuntimeException"))
        }

        exceptionHandlerRegistry.register(Exception::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive("Exception"))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("RuntimeException", response.payload.jsonPrimitive.content)
    }

    @Test
    fun testHandleDefaultError() {
        val exceptionHandlerRegistry = exceptionHandler()

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("UNHANDLED_ERROR", response.payload.jsonObject["code"]!!.jsonPrimitive.content)
    }

    @Test
    fun testBypassExceptionHandler() {
        val exceptionHandlerRegistry = bypassExceptionHandler()

        val cause = RuntimeException("Some error")
        val event = EventBuilderForTest.buildRequestEvent()

        val exception = assertThrows(BypassedException::class.java) {
            exceptionHandlerRegistry.handleException(cause, event, mockk(relaxed = true))
        }

        assertEquals(cause, exception.exception)
        assertEquals(event, exception.request)
    }

    @Test
    fun testBypassExceptionHandlerWithoutWrappingException() {
        val exceptionHandlerRegistry = bypassExceptionHandler(false)

        val cause = RuntimeException("Some error")
        val event = EventBuilderForTest.buildRequestEvent()

        val exception = assertThrows(RuntimeException::class.java) {
            exceptionHandlerRegistry.handleException(cause, event, mockk(relaxed = true))
        }

        assertEquals(cause, exception)
    }
}
