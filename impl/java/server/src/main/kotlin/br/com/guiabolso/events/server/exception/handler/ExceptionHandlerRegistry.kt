package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.tracing.Tracer
import org.slf4j.LoggerFactory

/**
 * To keep the same behaviour of version 5.x.x or older use [ExceptionHandlerRegistryFactory.exceptionHandler]
 */
class ExceptionHandlerRegistry internal constructor() {

    private val logger = LoggerFactory.getLogger(ExceptionHandlerRegistry::class.java)!!
    private val handlers = mutableMapOf<Class<*>, EventExceptionHandler<Throwable>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(clazz: Class<T>, handler: EventExceptionHandler<T>) {
        handlers[clazz] = handler as EventExceptionHandler<Throwable>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(clazz: Class<T>, handler: suspend (T, RequestEvent, Tracer) -> ResponseEvent) {
        register(
            clazz,
            LambdaEventExceptionHandler(handler as suspend (Throwable, RequestEvent, Tracer) -> ResponseEvent)
        )
    }

    suspend fun <T : Throwable> handleException(e: T, event: RequestEvent, tracer: Tracer): ResponseEvent {
        val handler = handlerFor(e)

        return if (handler != null) {
            handler.handleException(e, event, tracer)
        } else {
            logger.error("Error processing event.", e)
            tracer.notifyError(e, false)
            EventBuilder.errorFor(
                event,
                EventErrorType.Generic,
                EventMessage("UNHANDLED_ERROR", mapOf())
            )
        }
    }

    private fun handlerFor(e: Throwable): EventExceptionHandler<Throwable>? {
        return handlers[handlers.keys.firstOrNull { it.isAssignableFrom(e.javaClass) }]
    }
}
