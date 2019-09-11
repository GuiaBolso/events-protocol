package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.engine.TracerEngine
import java.io.Closeable

class CompositeTracerEngine(
    private var tracers: List<TracerEngine<*>>
) : TracerEngine<Map<TracerEngine<*>, Any>> {

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
        val finalValue:String = value.joinToString(",")
        addProperty(key, finalValue)
    }

    override fun <T> executeAndRecordTime(name: String, block: (MutableMap<String, String>) -> T): T {
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
        tracers.forEach {
            it.recordExecutionTime(name, elapsedTime, context)
        }
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        tracers.forEach { it.notifyError(exception, expected) }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracers.forEach { it.notifyError(message, params, expected) }
    }

    override fun extractContext(): Map<TracerEngine<*>, Any> {
        return tracers.map { it to it.extractContext()!! }.toMap()
    }

    @Suppress("UNCHECKED_CAST")
    override fun withContext(context: Any): Closeable {
        context as Map<TracerEngine<*>, Any>

        return CompositeTracerEngineCloseable(
            context.map { (engine, c) -> engine.withContext(c) }.toList()
        )
    }

    override fun withContext(context: Map<TracerEngine<*>, Any>, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {
        tracers.forEach { it.clear() }
    }

}

