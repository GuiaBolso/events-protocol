package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.metric.MetricReporter
import org.slf4j.LoggerFactory

class ExceptionHandlerRegistry {

    private val logger = LoggerFactory.getLogger(ExceptionHandlerRegistry::class.java)!!
    private val handlers = mutableMapOf<Class<*>, EventExceptionHandler<Throwable>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        handlers.put(clazz, handler as EventExceptionHandler<Throwable>)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(clazz: Class<T>, handler: (T, RequestEvent, MetricReporter) -> ResponseEvent) {
        handlers.put(clazz, LambdaEventExceptionHandler(handler as (Throwable, RequestEvent, MetricReporter) -> ResponseEvent))
    }

    fun <T : Throwable> handleException(e: T, event: RequestEvent, metricReporter: MetricReporter): ResponseEvent {
        val handler = handlerFor(e)

        return if (handler != null) {
            handler.handleException(e, event, metricReporter)
        } else {
            logger.error("Error processing event.", e)
            metricReporter.notifyError(e)
            EventBuilder.errorFor(
                    event,
                    EventErrorType.Generic,
                    EventMessage("UNHANDLED_ERROR", mapOf("message" to e.message, "exception" to ExceptionUtils.getStackTrace(e)))
            )
        }
    }

    private fun handlerFor(e: Throwable): EventExceptionHandler<Throwable>? {
        return handlers[handlers.keys.firstOrNull { it.isAssignableFrom(e.javaClass) }]
    }

}