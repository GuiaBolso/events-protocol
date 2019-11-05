package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.exception.EventException
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.tracing.Tracer

class ExceptionHandlerRegistry {

    private val handlers = mutableMapOf<Class<*>, EventExceptionHandler<Throwable>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(handler: EventExceptionHandler<T>) {
        handlers[handler.targetException] = handler as EventExceptionHandler<Throwable>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Throwable> register(clazz: Class<T>, handler: (T, RequestEvent, Tracer) -> ResponseEvent) {
        register(LambdaEventExceptionHandler(clazz, handler as (Throwable, RequestEvent, Tracer) -> ResponseEvent))
    }

    fun <T : Throwable> handleException(e: T, event: RequestEvent, tracer: Tracer): ResponseEvent {
        val handler = handlerFor(e)

        return if (handler != null) {
            handler.handleException(e, event, tracer)
        } else if (e is EventException) {
            tracer.notifyError(e, e.expected)
            errorFor(
                event = event,
                type = e.type,
                message = e.eventMessage
            )
        } else {
            tracer.notifyError(e, false)
            errorFor(
                event = event,
                type = EventErrorType.Generic,
                message = EventMessage("UNHANDLED_ERROR", mapOf("message" to e.message))
            )
        }
    }

    private fun handlerFor(e: Throwable): EventExceptionHandler<Throwable>? {
        return handlers[handlers.keys.firstOrNull { it.isAssignableFrom(e.javaClass) }]
    }
}