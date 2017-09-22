package br.com.guiabolso.events.exception

import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.ResponseEvent

object ExceptionHandlerRegistry {

    private val handlers = hashMapOf<Class<*>, EventExceptionHandler<Throwable>>()

    @JvmStatic
    fun <T : Throwable> register(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        handlers.put(clazz, handler as EventExceptionHandler<Throwable>)
    }

    @JvmStatic
    fun <T : Throwable> canHandle(e: T): Boolean {
        return e::class.java in handlers
    }

    @JvmStatic
    fun <T : Throwable> handleException(e: T, event: Event, metricReporter: MetricReporter): ResponseEvent {
        if (!canHandle(e)) throw IllegalStateException("Cannot handle exception ${e::class.java.simpleName}")

        return handlers[e::class.java]!!.handleException(e, event, metricReporter)
    }

}

interface EventExceptionHandler<in T : Throwable> {

    fun handleException(exception: T, event: Event, metricReporter: MetricReporter): ResponseEvent

}