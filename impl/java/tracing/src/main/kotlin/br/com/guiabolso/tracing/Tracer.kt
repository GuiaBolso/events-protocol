package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.engine.TracerEngine
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

interface Tracer {

    /**
     * Defines the operation name.
     *
     * @param name operation name.
     * @since 2.0.0
     */
    fun setOperationName(name: String)

    /**
     * Add a key/value pair to the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: String?)

    /**
     * Add a key/value pair to the local root span at the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.10.1
     */
    fun addRootProperty(key: String, value: String?)

    /**
     * Add a key/value pair to the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: Number?)

    /**
     * Add a key/value pair to the local root span at the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.10.1
     */
    fun addRootProperty(key: String, value: Number?)

    /**
     * Add a key/value pair to the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: Boolean?)

    /**
     * Add a key/value pair to the local root span at the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.10.1
     */
    fun addRootProperty(key: String, value: Boolean?)

    /**
     * Add a key/value pair to the current traced operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.6.1
     */
    fun addProperty(key: String, value: List<*>)

    /**
     * Run a block of code and register its execution time using the current Engine, under the received metric name.
     * The block of code receives a MutableMap context to save specific metric tags, and return an object of type T.
     *
     * @param name Metric name that registers execution time.
     * @param block Block of code to be executed.
     * @since 2.2.0
     */
    fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(executor: ExecutorService, task: Callable<T>): Future<T>

    /**
     * Track a scheduled task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @param delay The delay.
     * @param unit The time unit.
     * @since 3.1.0
     */
    fun <T> schedule(executor: ScheduledExecutorService, task: Callable<T>, delay: Long, unit: TimeUnit): ScheduledFuture<T>

    /**
     * Notice an error and report it to the tracer.
     *
     * @param exception The exception to be reported.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.0.0
     */
    fun notifyError(exception: Throwable, expected: Boolean)

    /**
     * Notice an error and report it to the tracer.
     *
     * @param message Error message.
     * @param params Custom parameters to include in the traced error. May be null.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.0.0
     */
    fun notifyError(message: String, params: Map<String, String?>, expected: Boolean)

    /**
     * Returns the tracing engine of this tracer
     */
    fun getTracerEngine(): TracerEngine<*>

    /**
     * Cleans the tracer state.
     */
    fun clear()
}
