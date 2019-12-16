package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable

class TracingCallable<T>(
    private val tracerEngine: TracerEngine<*>,
    private val tracingContext: Any,
    private val eventContext: EventContext?,
    private val callable: Callable<T>
) : Callable<T> {

    override fun call(): T {
        EventContextHolder.setContext(eventContext)
        tracerEngine.withContext(tracingContext).use {
            try {
                return callable.call()
            } finally {
                EventContextHolder.clean()
            }
        }
    }

}