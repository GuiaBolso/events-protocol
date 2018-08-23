package br.com.guiabolso.metrics.factory

import java.io.Closeable

data class CompositeMetricReporterEngineCloseable(val context: List<Closeable>) : Closeable {
    override fun close() {
        context.forEach {
            try {
                it.close()
            } catch (_: Exception) {
            }
        }
    }
}