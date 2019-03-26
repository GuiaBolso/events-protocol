package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.async.AsyncExecutor
import br.com.guiabolso.tracing.engine.TimeRecorderTracerEngine

class TimeRecorderTracerImpl(
        private val tracerEngine: TimeRecorderTracerEngine<*>,
        asyncExecutor: AsyncExecutor
): TimeRecorderTracer, TracerImpl(tracerEngine, asyncExecutor) {

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        return tracerEngine.executeAndRecordTime(name, block)
    }

}