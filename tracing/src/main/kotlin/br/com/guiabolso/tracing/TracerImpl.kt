package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.async.AsyncExecutor
import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class TracerImpl(
    private val tracerEngine: TracerEngine<*>,
    private val asyncExecutor: AsyncExecutor
) : Tracer {

    override fun getTracerEngine(): TracerEngine<*> {
        return tracerEngine
    }

    override fun setOperationName(name: String) {
        tracerEngine.setOperationName(name)
    }

    override fun addProperty(key: String, value: String?) {
        tracerEngine.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        tracerEngine.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        tracerEngine.addProperty(key, value)
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        return tracerEngine.executeAndRecordTime(name, block)
    }

    override fun <T> executeAsync(executor: ExecutorService, task: () -> T): Future<T> {
        return asyncExecutor.executeAsync(tracerEngine, executor, task)
    }

    override fun <T> executeAsync(executor: ExecutorService, task: Callable<T>): Future<T> {
        return asyncExecutor.executeAsync(tracerEngine, executor, task)
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        tracerEngine.notifyError(exception, expected)
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracerEngine.notifyError(message, params, expected)
    }

    override fun clear() {
        tracerEngine.clear()
    }

}