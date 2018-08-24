package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import java.io.Closeable

class CompositeMetricReporterEngine(
        private var metricReporters: List<MetricReporterEngine<*>>
) : MetricReporterEngine<Map<MetricReporterEngine<*>, Any>> {

    override fun setOperationName(name: String) {
        metricReporters.forEach { it.setOperationName(name) }
    }

    override fun addProperty(key: String, value: String?) {
        metricReporters.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Number?) {
        metricReporters.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Boolean?) {
        metricReporters.forEach { it.addProperty(key, value) }
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        metricReporters.forEach { it.notifyError(exception, expected) }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        metricReporters.forEach { it.notifyError(message, params, expected) }
    }

    override fun extractContext(): Map<MetricReporterEngine<*>, Any> {
        return metricReporters.map { it to it.extractContext()!! }.toMap()
    }

    @Suppress("UNCHECKED_CAST")
    override fun withContext(context: Any): Closeable {
        context as Map<MetricReporterEngine<*>, Any>

        return CompositeMetricReporterEngineCloseable(
                context.map { (engine, c) -> engine.withContext(c) }.toList()
        )
    }

    override fun withContext(context: Map<MetricReporterEngine<*>, Any>, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {
        metricReporters.forEach { it.clear() }
    }

}

