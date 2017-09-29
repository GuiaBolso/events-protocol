package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.metric.MetricReporter
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