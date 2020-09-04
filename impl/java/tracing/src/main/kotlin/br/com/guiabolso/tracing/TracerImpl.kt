package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.wrapper.ExecutorServiceWrapper
import br.com.guiabolso.tracing.wrapper.ScheduledExecutorServiceWrapper
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

class TracerImpl(
    private var tracers: List<TracerEngine>,
    private val contextManagers: List<ThreadContextManager<*>>
) : Tracer {

    override fun setOperationName(name: String) {
        tracers.forEach { it.setOperationName(name) }
    }

    override fun addProperty(key: String, value: String?) {
        tracers.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Number?) {
        tracers.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Boolean?) {
        tracers.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: List<*>) {
        tracers.forEach { it.addProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: String?) {
        tracers.forEach { it.addRootProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: Number?) {
        tracers.forEach { it.addRootProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        tracers.forEach { it.addRootProperty(key, value) }
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        val start = System.currentTimeMillis()
        val context = mutableMapOf<String, String>()
        try {
            return block(context)
        } finally {
            val elapsedTime = System.currentTimeMillis() - start
            recordExecutionTime(name, elapsedTime, context)
        }
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        tracers.forEach { it.recordExecutionTime(name, elapsedTime, context) }
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        tracers.forEach { it.notifyError(exception, expected) }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracers.forEach { it.notifyError(message, params, expected) }
    }

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        tracers.forEach { it.notifyRootError(exception, expected) }
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracers.forEach { it.notifyRootError(message, params, expected) }
    }

    override fun clear() {
        tracers.forEach { it.clear() }
    }

    override fun wrap(executorService: ExecutorService): ExecutorService {
        return ExecutorServiceWrapper(contextManagers, executorService)
    }

    override fun wrap(scheduledExecutorService: ScheduledExecutorService): ScheduledExecutorService {
        return ScheduledExecutorServiceWrapper(contextManagers, scheduledExecutorService)
    }
}
