package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.metric.MetricReporter
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test
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
                Mockito.mock(MetricReporter::class.java)
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
                Mockito.mock(MetricReporter::class.java)
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
                Mockito.mock(MetricReporter::class.java)
        )

        assertEquals("RuntimeException", response.payload.asString)
    }


    @Test
    fun testHandleDefaultError() {
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

        val response = exceptionHandlerRegistry.handleException(
                RuntimeException("Some error"),
                EventBuilderForTest.buildRequestEvent(),
                Mockito.mock(MetricReporter::class.java)
        )

        assertEquals("UNHANDLED_ERROR", response.payload.asJsonObject["code"].asString)
    }

}