package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.server.exception.ExceptionUtils
import datadog.trace.api.DDTags.ERROR_MSG
import datadog.trace.api.DDTags.ERROR_STACK
import datadog.trace.api.DDTags.ERROR_TYPE
import datadog.trace.api.DDTags.RESOURCE_NAME
import io.opentracing.util.GlobalTracer

class DatadogMetricReporter : MetricReporter {

    override fun startProcessingEvent(event: Event) {
        addProperty(RESOURCE_NAME, "${event.name}:V${event.version}")
        addProperty("EventID", event.id)
        addProperty("FlowID", event.flowId)
        addProperty("UserID", event.identity.get("userId")?.asString ?: "unknown")
        addProperty("Origin", event.metadata.get("origin")?.asString ?: "unknown")
    }

    override fun eventProcessFinished(event: Event) {

    }

    override fun addProperty(key: String, value: String) {
        val tracer = GlobalTracer.get()!!
        tracer.activeSpan()?.setTag(key, value)
    }

    override fun notifyError(exception: Throwable) {
        notifyError(exception, true)
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        addProperty("error", (!expected).toString())
        addProperty(ERROR_MSG, exception.message ?: "Empty message")
        addProperty(ERROR_TYPE, exception.javaClass.name)
        addProperty(ERROR_STACK, ExceptionUtils.getStackTrace(exception))
    }

}
