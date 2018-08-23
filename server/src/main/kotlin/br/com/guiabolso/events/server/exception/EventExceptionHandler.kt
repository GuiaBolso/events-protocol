package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.metrics.MetricReporter

interface EventExceptionHandler<in T : Throwable> {

    fun handleException(exception: T, event: RequestEvent, metricReporter: MetricReporter): ResponseEvent

}