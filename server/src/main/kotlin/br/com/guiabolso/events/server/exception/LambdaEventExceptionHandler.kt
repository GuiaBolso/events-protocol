package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.metric.MetricReporter

class LambdaEventExceptionHandler<in T : Throwable>(private val func: (T, RequestEvent, MetricReporter) -> ResponseEvent) : EventExceptionHandler<T> {

    override fun handleException(exception: T, event: RequestEvent, metricReporter: MetricReporter) = func(exception, event, metricReporter)

}