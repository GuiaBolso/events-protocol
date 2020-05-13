package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import com.newrelic.api.agent.Trace
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class NewRelicAsyncExecutor : AsyncExecutor {

    override fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: () -> T): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            asyncTask(engine, context, task)
        }))
    }

    override fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T> {
        val context = engine.extractContext()!!
        return executor.submit((Callable {
            asyncTask(engine, context, task)
        }))
    }

    // This is required to NewRelic works
    @Trace(async = true)
    private fun <T> asyncTask(engine: TracerEngine<*>, context: Any, task: () -> T): T {
        return engine.withContext(context).use {
            task()
        }
    }

    // This is required to NewRelic works
    @Trace(async = true)
    private fun <T> asyncTask(engine: TracerEngine<*>, context: Any, task: Callable<T>): T {
        return engine.withContext(context).use {
            task.call()
        }
    }
}
