package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

interface Tracer : TracerEngine {

    fun wrap(executorService: ExecutorService): ExecutorService

    fun wrap(scheduledExecutorService: ScheduledExecutorService): ScheduledExecutorService
}
