package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class DefaultAsyncExecutor : AsyncExecutor {

    override fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: () -> T): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            engine.withContext(context).use {
                task()
            }
        }))
    }

    override fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            engine.withContext(context).use {
                task.call()
            }
        }))
    }

}