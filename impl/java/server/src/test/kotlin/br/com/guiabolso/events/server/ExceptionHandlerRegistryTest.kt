package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.server.exception.BypassedException
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.bypassExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistryFactory.exceptionHandler
import br.com.guiabolso.tracing.Tracer
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito

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
            Mockito.mock(Tracer::class.java)
        )

        assertEquals("Some error", response.payload.asString)
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
            Mockito.mock(Tracer::class.java)
        )

        assertEquals("Exception", response.payload.asString)
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
            Mockito.mock(Tracer::class.java)
        )

        assertEquals("RuntimeException", response.payload.asString)
    }

    @Test
    fun testHandleDefaultError() {
        val exceptionHandlerRegistry = exceptionHandler()

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            Mockito.mock(Tracer::class.java)
        )

        assertEquals("UNHANDLED_ERROR", response.payload.asJsonObject["code"].asString)
    }

    @Test
    fun testBypassExceptionHandler() {
        val exceptionHandlerRegistry = bypassExceptionHandler()

        val cause = RuntimeException("Some error")
        val event = EventBuilderForTest.buildRequestEvent()

        val exception = assertThrows(BypassedException::class.java) {
            exceptionHandlerRegistry.handleException(cause, event, Mockito.mock(Tracer::class.java))
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
            exceptionHandlerRegistry.handleException(cause, event, Mockito.mock(Tracer::class.java))
        }

        assertEquals(cause, exception)
    }
}
