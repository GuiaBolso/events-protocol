package br.com.guiabolso.events.context

import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.runBlocking

/**
 *  Compatibility layer for injecting the EventContext in a CoroutineScope
 */
object EventCoroutineContextForwarder {

    private val holder = ThreadLocal<EventContext>()

    val current: EventContext
        get() = holder.get() ?: EventContext()

    fun <R> withCoroutineContext(
        context: EventContext = EventThreadContextManager.current,
        func: suspend () -> R
    ): R {
        return runBlocking(holder.asContextElement(context)) {
            func()
        }
    }
}
