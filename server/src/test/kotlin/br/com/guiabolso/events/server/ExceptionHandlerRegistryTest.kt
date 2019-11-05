package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.exception.EventException
import br.com.guiabolso.events.model.EventErrorType.Generic
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.tracing.Tracer
import com.google.gson.JsonPrimitive
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class ExceptionHandlerRegistryTest {

    @Test
    fun testCanRegisterExceptionHandler() {
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

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
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

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
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

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
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()
        val tracer = Mockito.mock(Tracer::class.java)

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            tracer
        )

        val message = response.payloadAs<EventMessage>()
        assertEquals("UNHANDLED_ERROR", message.code)
        assertEquals(mapOf("message" to "Some error"), message.parameters)
        assertEquals(Generic, response.getErrorType())

        verify(tracer, times(1)).notifyError(any(), eq(false))
    }

    @Test
    fun testHandleEventException() {
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()
        val tracer = Mockito.mock(Tracer::class.java)

        val response = exceptionHandlerRegistry.handleException(
            EventException("CODE", mapOf("param" to 42.0), Generic, true, null),
            EventBuilderForTest.buildRequestEvent(),
            tracer
        )

        val message = response.payloadAs<EventMessage>()
        assertEquals("CODE", message.code)
        assertEquals(mapOf("param" to 42.0), message.parameters)
        assertEquals(Generic, response.getErrorType())

        verify(tracer, times(1)).notifyError(any(), eq(true))
    }

}