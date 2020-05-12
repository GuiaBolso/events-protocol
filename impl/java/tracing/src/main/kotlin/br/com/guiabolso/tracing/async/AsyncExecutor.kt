package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface AsyncExecutor {

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: () -> T): Future<T>

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T>
}
