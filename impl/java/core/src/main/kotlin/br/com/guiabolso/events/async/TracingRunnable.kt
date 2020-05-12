package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.engine.TracerEngine

class TracingRunnable<C>(
    private val tracerEngine: TracerEngine<C>,
    private val tracingContext: Any,
    private val eventContext: EventContext?,
    private val runnable: Runnable
) : Runnable {

    override fun run() {
        EventContextHolder.setContext(eventContext)
        tracerEngine.withContext(tracingContext).use {
            try {
                return runnable.run()
            } finally {
                EventContextHolder.clean()
            }
        }
    }
}
