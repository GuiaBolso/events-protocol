package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventExceptionHandler
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.metric.MetricReporter
import com.google.gson.JsonPrimitive
import org.junit.Assert
import org.junit.Test

class ExceptionHandlerRegistryTest {


    @Test
    fun testCanRegisterExceptionHandler() {
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

        exceptionHandlerRegistry.register(RuntimeException::class.java, object : EventExceptionHandler<RuntimeException> {

            override fun handleException(exception: RuntimeException, event: Event, metricReporter: MetricReporter): ResponseEvent {
                return EventBuilderForTest.buildResponseEvent().copy(payload = JsonPrimitive(exception.message))
            }

        })

        val response = exceptionHandlerRegistry.handleException(
                RuntimeException("Some error"),
                EventBuilderForTest.buildRequestEvent(),
                NoOpMetricReporter())

        Assert.assertEquals("Some error", response.payload.asString)
    }


    @Test
    fun testHandleDefaultError() {
        val exceptionHandlerRegistry = ExceptionHandlerRegistry()

        val response = exceptionHandlerRegistry.handleException(
                RuntimeException("Some error"),
                EventBuilderForTest.buildRequestEvent(),
                NoOpMetricReporter())

        Assert.assertEquals("UNHANDLED_ERROR", response.payload.asJsonObject["code"].asString)
    }
}


class NoOpMetricReporter : MetricReporter {
    override fun startProcessingEvent(event: Event) {
        //NOTHING
    }

    override fun eventProcessFinished(event: Event) {
        //NOTHING
    }

    override fun addProperty(key: String, value: String) {
        //NOTHING
    }

    override fun notifyError(exception: Throwable) {
        //NOTHING
    }

}