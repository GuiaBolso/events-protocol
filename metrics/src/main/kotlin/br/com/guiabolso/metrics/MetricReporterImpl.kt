package br.com.guiabolso.metrics

import br.com.guiabolso.metrics.async.AsyncExecutor
import br.com.guiabolso.metrics.engine.MetricReporterEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class MetricReporterImpl(
        private val metricReporterEngine: MetricReporterEngine<*>,
        private val asyncExecutor: AsyncExecutor
) : MetricReporter {

    override fun setOperationName(name: String) {
        metricReporterEngine.setOperationName(name)
    }

    override fun addProperty(key: String, value: String?) {
        metricReporterEngine.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        metricReporterEngine.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        metricReporterEngine.addProperty(key, value)
    }

    override fun <T> executeAsync(executor: ExecutorService, task: () -> T): Future<T> {
        return asyncExecutor.executeAsync(metricReporterEngine, executor, task)
    }

    override fun <T> executeAsync(executor: ExecutorService, task: Callable<T>): Future<T> {
        return asyncExecutor.executeAsync(metricReporterEngine, executor, task)
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        metricReporterEngine.notifyError(exception, expected)
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        metricReporterEngine.notifyError(message, params, expected)
    }

    override fun clear() {
        metricReporterEngine.clear()
    }

}