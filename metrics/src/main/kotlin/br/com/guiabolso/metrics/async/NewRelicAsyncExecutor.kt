package br.com.guiabolso.metrics.async

import br.com.guiabolso.metrics.engine.MetricReporterEngine
import com.newrelic.api.agent.Trace
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class NewRelicAsyncExecutor : AsyncExecutor {

    override fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: () -> T): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            asyncTask(engine, context, task)
        }))
    }

    override fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            engine.withContext(context).use {
                asyncTask(engine, context, task)
            }
        }))
    }

    //This is required to NewRelic works
    @Trace(async = true)
    private fun <T> asyncTask(engine: MetricReporterEngine<*>, context: Any, task: () -> T): T {
        return engine.withContext(context).use {
            task()
        }
    }

    //This is required to NewRelic works
    @Trace(async = true)
    private fun <T> asyncTask(engine: MetricReporterEngine<*>, context: Any, task: Callable<T>): T {
        return engine.withContext(context).use {
            task.call()
        }
    }

}