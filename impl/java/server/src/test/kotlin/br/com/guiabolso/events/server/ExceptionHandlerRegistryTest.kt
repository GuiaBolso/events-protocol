package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.JsonPrimitive
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.asPrimitiveStringNode
import br.com.guiabolso.events.json.asString
import br.com.guiabolso.events.json.asTreeNode
import br.com.guiabolso.events.json.getAsPrimitiveStringNode
import br.com.guiabolso.events.json.getValue
import br.com.guiabolso.events.server.exception.BypassedException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.bypassExceptionHandler
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.exceptionHandler
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ExceptionHandlerRegistryTest {

    @Test
    fun testCanRegisterExceptionHandler() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler()

        exceptionHandlerRegistry.register(RuntimeException::class.java) { exception, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = mapper.toJsonTree(exception.message))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("Some error", response.payload.asPrimitiveStringNode().value)
    }

    @Test
    fun testExceptionPriority() = runBlocking {
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

        assertEquals("Exception", response.payload.asString())
    }

    @Test
    fun testExceptionPriority2() = runBlocking {
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

        assertEquals("RuntimeException", response.payload.asString())
    }

    @Test
    fun testHandleDefaultError() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler()

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            EventBuilderForTest.buildRequestEvent(),
            mockk(relaxed = true)
        )

        assertEquals("UNHANDLED_ERROR", response.payload.asTreeNode().getAsPrimitiveStringNode("code").value)
    }

    @Test
    fun testBypassExceptionHandler() {
        val exceptionHandlerRegistry = bypassExceptionHandler()

        val cause = RuntimeException("Some error")
        val event = EventBuilderForTest.buildRequestEvent()

        val exception = assertThrows(BypassedException::class.java) {
            runBlocking {
                exceptionHandlerRegistry.handleException(cause, event, mockk(relaxed = true))
            }
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
            runBlocking {
                exceptionHandlerRegistry.handleException(cause, event, mockk(relaxed = true))
            }
        }

        assertEquals(cause, exception)
    }
}
