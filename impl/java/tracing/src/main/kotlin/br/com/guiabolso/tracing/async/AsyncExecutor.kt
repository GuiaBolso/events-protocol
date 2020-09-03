package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

interface AsyncExecutor {

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(engine: TracerEngine<*>, executor: ExecutorService, task: Callable<T>): Future<T>

    /**
     * Track a scheduled task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @param delay The delay.
     * @param unit The time unit.
     * @since 3.1.0
     */
    fun <T> schedule(engine: TracerEngine<*>, executor: ScheduledExecutorService, task: Callable<T>, delay: Long, unit: TimeUnit): ScheduledFuture<T>
}
