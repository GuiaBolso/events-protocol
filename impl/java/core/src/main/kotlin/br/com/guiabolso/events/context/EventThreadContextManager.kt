package br.com.guiabolso.events.context

import br.com.guiabolso.tracing.context.ThreadContextManager
import java.io.Closeable

object EventThreadContextManager : ThreadContextManager<EventContext> {

    override val type = EventContext::class.java

    private val holder = ThreadLocal<EventContext>()

    val current: EventContext
        get() = holder.get() ?: EventContext()

    override fun extract(): EventContext {
        return current
    }

    override fun withContext(context: EventContext): Closeable {
        holder.set(context)
        return Closeable { holder.remove() }
    }
}
