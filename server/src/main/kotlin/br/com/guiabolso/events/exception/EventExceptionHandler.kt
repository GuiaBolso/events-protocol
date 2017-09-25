package br.com.guiabolso.events.exception

import br.com.guiabolso.events.EventProcessor
import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.ResponseEvent
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory

object ExceptionHandlerRegistry {

    private val logger = LoggerFactory.getLogger(EventProcessor::class.java)!!

    private val handlers = hashMapOf<Class<*>, EventExceptionHandler<Throwable>>()

    @JvmStatic
    fun <T : Throwable> register(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        handlers.put(clazz, handler as EventExceptionHandler<Throwable>)
    }

    @JvmStatic
    fun <T : Throwable> handleException(e: T, event: Event, metricReporter: MetricReporter): Event {
        return when (canHandle(e)) {
            false -> {
                logger.error("Error processing event.", e)
                metricReporter.notifyError(e)
                errorFor(
                        event, EventErrorType.Generic(),
                        EventMessage("UNHANDLED_ERROR", mapOf("message" to e.message, "exception" to ExceptionUtils.getStackTrace(e)))
                )
            }
            else -> handlers[e::class.java]!!.handleException(e, event, metricReporter)
        }
    }

    private fun <T : Throwable> canHandle(e: T): Boolean {
        return e::class.java in handlers
    }
}

interface EventExceptionHandler<in T : Throwable> {

    fun handleException(exception: T, event: Event, metricReporter: MetricReporter): ResponseEvent

}