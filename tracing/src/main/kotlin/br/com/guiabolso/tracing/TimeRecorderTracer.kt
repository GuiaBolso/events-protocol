package br.com.guiabolso.tracing

interface TimeRecorderTracer: Tracer {

    /**
     * Run a block of code and register its execution time using the current Engine, under the received metric name.
     * The block of code receives a MutableMap context to save specific metric tags, and return an object of type T.
     *
     * @param name Metric name that registers execution time.
     * @param block Block of code to be executed.
     * @since 2.2.0
     */
    fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T

}