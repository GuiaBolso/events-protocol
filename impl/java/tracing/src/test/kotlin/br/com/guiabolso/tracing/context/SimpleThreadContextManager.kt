package br.com.guiabolso.tracing.context

import java.io.Closeable

class SimpleThreadContextManager : ThreadContextManager<String> {

    override val type = String::class.java

    private val threadLocal = ThreadLocal<String>()

    override fun extract() = threadLocal.get() ?: "None"

    override fun withContext(context: String): Closeable {
        threadLocal.set(context)
        return Closeable { threadLocal.remove() }
    }
}
