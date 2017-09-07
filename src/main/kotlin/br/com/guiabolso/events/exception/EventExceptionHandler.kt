package br.com.guiabolso.events.exception

import br.com.guiabolso.events.metric.MetricReporter
import br.com.guiabolso.events.model.Event

interface EventExceptionHandler<in T : Exception> {

    fun handleException(metricReporter: MetricReporter, event: Event, exception: T)

}