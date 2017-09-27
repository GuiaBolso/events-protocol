package br.com.guiabolso.events.server.metric

import br.com.guiabolso.events.model.Event

interface MetricReporter {

    fun startProcessingEvent(event: Event)

    fun eventProcessFinished(event: Event)

    fun addProperty(key: String, value: String)

    fun notifyError(exception: Throwable)

}