package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event

class CompositeMetricReporter(private vararg val reporters: MetricReporter) : MetricReporter {

    override fun startProcessingEvent(event: Event) {
        reporters.forEach { it.startProcessingEvent(event) }
    }

    override fun eventProcessFinished(event: Event) {
        reporters.forEach { it.eventProcessFinished(event) }
    }

    override fun addProperty(key: String, value: String) {
        reporters.forEach { it.addProperty(key, value) }
    }

    override fun notifyError(exception: Throwable) {
        reporters.forEach { it.notifyError(exception) }
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        reporters.forEach { it.notifyError(exception, expected) }
    }

}