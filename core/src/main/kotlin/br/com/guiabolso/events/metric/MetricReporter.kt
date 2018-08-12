package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event

interface MetricReporter {

    /**
     * Starts metricizing an event.
     *
     * @param event The event to be metricized.
     * @since 1.0.0
     */
    fun startProcessingEvent(event: Event)

    /**
     * Stops metricizing an event.
     *
     * @param event The event being metricized.
     * @since 1.0.0
     */
    fun eventProcessFinished(event: Event)

    /**
     * Add a key/value pair to the current metric transaction. These should be reported in errors and tracings.
     *
     * @param key Custom parameter key.
     * @param value Custom parameter value.
     * @since 1.0.0
     */
    fun addProperty(key: String, value: String)

    /**
     * Notice an error and report it to the metric reporter.
     *
     * @param exception The exception to be reported.
     * @since 1.0.0
     */
    fun notifyError(exception: Throwable)

    /**
     * Notice an error and report it to the metric reporter.
     *
     * Expected errors should not increment an application's error count
     *
     * @param exception The exception to be reported.
     * @param expected true if this error is expected, false otherwise.
     * @since 1.2.0
     */
    fun notifyError(exception: Throwable, expected: Boolean)

}