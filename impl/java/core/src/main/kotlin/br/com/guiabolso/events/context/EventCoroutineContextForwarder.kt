package br.com.guiabolso.events.context

import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext

/**
 *  Compatibility layer for injecting the EventContext in a CoroutineScope
 */
object EventCoroutineContextForwarder {

    private val holder = ThreadLocal<EventContext>()

    val current: EventContext
        get() = holder.get() ?: EventContext()

    suspend fun <R> withCoroutineContext(
        context: EventContext = EventThreadContextManager.current,
        func: suspend () -> R
    ): R {
        return withContext(holder.asContextElement(context)) {
            func()
        }
    }
}
