package br.com.guiabolso.metrics.async

import br.com.guiabolso.metrics.engine.MetricReporterEngine
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
    fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: () -> T): Future<T>

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(engine: MetricReporterEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T>

}