package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class DefaultAsyncExecutor : AsyncExecutor {

    override fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            engine.withContext(context).use {
                task.call()
            }
        }))
    }

    override fun <T> schedule(engine: TracerEngine<*>, executor: ScheduledExecutorService, task: Callable<T>, delay: Long, unit: TimeUnit): ScheduledFuture<T> {
        val context = engine.extractContext()!!
        return executor.schedule((Callable {
            engine.withContext(context).use {
                task.call()
            }
        }), delay, unit)
    }
}
