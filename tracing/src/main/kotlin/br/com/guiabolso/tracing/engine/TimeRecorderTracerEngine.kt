package br.com.guiabolso.tracing.engine

interface TimeRecorderTracerEngine<C>: TracerEngine<C> {

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

}