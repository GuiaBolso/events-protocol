package br.com.guiabolso.tracing.engine

import java.io.Closeable

interface TracerEngine<C> {

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
     * @since 2.6.0
     */
    fun addProperty(key: String, value: List<*>)

    /**
     * Register an execution time using the current Engine, under the received metric name. The metric will be
     * registered with any tags that come within the context Map
     *
     * @param name Metric name that registers execution time.
     * @param elapsedTime Number representing the execution time.
     * @param context Map of tags to be registered within the metric.
     * @since 2.2.0
     */
    fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>)

    /**
     * Run a block of code and register its execution time using the current Engine, under the received metric name.
     * The block of code receives a MutableMap context to save specific metric tags, and return an object of type T.
     *
     * @param name Metric name that registers execution time.
     * @param block Block of code to be executed.
     * @since 2.2.0
     */
    fun <T> executeAndRecordTime(name: String, block: (MutableMap<String, String>) -> T): T

    /**
     * Notice an error and report it to the tracer.
     *
     * @param exception The exception to be reported.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.0.0
     */
    fun notifyError(exception: Throwable, expected: Boolean)

    /**
     * Notice an error and report it to the tracer at the root span.
     *
     * @param exception The exception to be reported.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.10.1
     */
    fun notifyRootError(exception: Throwable, expected: Boolean)

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
     * Notice an error and report it to the tracer at the root span.
     *
     * @param message Error message.
     * @param params Custom parameters to include in the traced error. May be null.
     * @param expected true if this error is expected, false otherwise.
     * @since 2.10.1
     */
    fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean)

    /**
     * Extract the current trace context
     */
    fun extractContext(): C

    /**
     * Uses the current trace context. After the execution the the context must be closed.
     * Its safer to use {@link #withContext(context: C, func: () -> Any) withContext}
     */
    fun withContext(context: Any): Closeable

    /**
     * Uses the current trace context
     */
    fun withContext(context: C, func: () -> Any)

    /**
     * Cleans the tracer state.
     */
    fun clear()
}
