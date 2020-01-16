package br.com.guiabolso.tracing.factory

import java.io.Closeable

data class CompositeTracerEngineCloseable(val context: List<Closeable>) : Closeable {
    override fun close() {
        context.forEach {
            try {
                it.close()
            } catch (_: Exception) {
            }
        }
    }
}