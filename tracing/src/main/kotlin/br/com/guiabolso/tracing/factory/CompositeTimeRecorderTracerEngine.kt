package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.engine.TimeRecorderTracerEngine
import br.com.guiabolso.tracing.engine.TracerEngine

class CompositeTimeRecorderTracerEngine(
        private var tracers: List<TimeRecorderTracerEngine<*>>
): CompositeTracerEngine(tracers), TimeRecorderTracerEngine<Map<TracerEngine<*>, Any>> {

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
}