package br.com.guiabolso.tracing.context

import java.io.Closeable

/**
 * Objects that manage contexts bounded to threads.
 * @see ThreadLocal
 */
interface ThreadContextManager<C> {

    val type: Class<C>

    /**
     * Extract the current trace context
     *
     * @since 4.0.0
     */
    fun extract(): C

    /**
     * Uses the current trace context. After the execution the the context must be closed.
     *
     * @since 4.0.0
     */
    fun withContext(context: C): Closeable

    /**
     * Uses the current trace context. After the execution the the context must be closed.
     *
     * @since 4.0.0
     */
    fun withUnsafeContext(context: Any): Closeable {
        return withContext(type.cast(context))
    }
}
