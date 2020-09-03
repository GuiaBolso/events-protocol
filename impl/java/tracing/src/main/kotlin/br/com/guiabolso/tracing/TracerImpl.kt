package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.wrapper.ExecutorServiceWrapper
import br.com.guiabolso.tracing.wrapper.ScheduledExecutorServiceWrapper
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

class TracerImpl(
    private val tracerEngine: TracerEngine,
    private val contextManagers: List<ThreadContextManager<*>>
) : Tracer {

    override fun setOperationName(name: String) {
        tracerEngine.setOperationName(name)
    }

    override fun addProperty(key: String, value: String?) {
        tracerEngine.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: String?) {
        tracerEngine.addRootProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Number?) {
        tracerEngine.addRootProperty(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        tracerEngine.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        tracerEngine.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        tracerEngine.addRootProperty(key, value)
    }

    override fun addProperty(key: String, value: List<*>) {
        tracerEngine.addProperty(key, value)
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        return tracerEngine.recordExecutionTime(name, block)
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        return tracerEngine.recordExecutionTime(name, elapsedTime, context)
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        tracerEngine.notifyError(exception, expected)
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracerEngine.notifyError(message, params, expected)
    }

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        tracerEngine.notifyRootError(exception, expected)
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracerEngine.notifyRootError(message, params, expected)
    }

    override fun clear() {
        tracerEngine.clear()
    }

    override fun wrap(executorService: ExecutorService): ExecutorService {
        return ExecutorServiceWrapper(contextManagers, executorService)
    }

    override fun wrap(scheduledExecutorService: ScheduledExecutorService): ScheduledExecutorService {
        return ScheduledExecutorServiceWrapper(contextManagers, scheduledExecutorService)
    }
}
