package br.com.guiabolso.tracing

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

interface MetricReporter {

    /**
     * Defines the operation name.
     *
     * @param name operation name.
     * @since 2.0.0
     */
    fun setOperationName(name: String)

    /**
     * Add a key/value pair to the current metric operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: String?)

    /**
     * Add a key/value pair to the current metric operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: Number?)

    /**
     * Add a key/value pair to the current metric operation. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 2.0.0
     */
    fun addProperty(key: String, value: Boolean?)

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(executor: ExecutorService, task: () -> T): Future<T>

    /**
     * Track an asynchronous task execution
     *
     * @param executor The desired executor.
     * @param task The task.
     * @since 2.0.0
     */
    fun <T> executeAsync(executor: ExecutorService, task: Callable<T>): Future<T>

    /**
     * Notice an error and report it to the metric reporter.
     *
     * @param exception The exception to be reported.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.0.0
     */
    fun notifyError(exception: Throwable, expected: Boolean)

    /**
     * Notice an error and report it to the metric reporter.
     *
     * @param message Error message.
     * @param params Custom parameters to include in the traced error. May be null.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.0.0
     */
    fun notifyError(message: String, params: Map<String, String?>, expected: Boolean)

    /**
     * Cleans the metric reporter state.
     */
    fun clear()

}