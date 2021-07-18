package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.wrapper.ExecutorServiceWrapper
import br.com.guiabolso.tracing.wrapper.ScheduledExecutorServiceWrapper
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

@Suppress("TooManyFunctions")
class TracerImpl(
    var engines: List<TracerEngine>,
    val contextManagers: List<ThreadContextManager<*>>
) : Tracer {

    override fun setOperationName(name: String) {
        engines.forEach { it.setOperationName(name) }
    }

    override fun addProperty(key: String, value: String?) {
        engines.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Number?) {
        engines.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: Boolean?) {
        engines.forEach { it.addProperty(key, value) }
    }

    override fun addProperty(key: String, value: List<*>) {
        engines.forEach { it.addProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: String?) {
        engines.forEach { it.addRootProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: Number?) {
        engines.forEach { it.addRootProperty(key, value) }
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        engines.forEach { it.addRootProperty(key, value) }
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

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: Map<String, String>) {
        engines.forEach { it.recordExecutionTime(name, elapsedTime, context) }
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        engines.forEach { it.notifyError(exception, expected) }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        engines.forEach { it.notifyError(message, params, expected) }
    }

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        engines.forEach { it.notifyRootError(exception, expected) }
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        engines.forEach { it.notifyRootError(message, params, expected) }
    }

    override fun clear() {
        engines.forEach { it.clear() }
    }

    override fun wrap(executorService: ExecutorService): ExecutorService {
        return ExecutorServiceWrapper(contextManagers, executorService)
    }

    override fun wrap(scheduledExecutorService: ScheduledExecutorService): ScheduledExecutorService {
        return ScheduledExecutorServiceWrapper(contextManagers, scheduledExecutorService)
    }
}
