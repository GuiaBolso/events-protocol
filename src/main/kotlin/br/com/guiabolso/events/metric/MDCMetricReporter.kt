package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event
import org.slf4j.MDC

class MDCMetricReporter : MetricReporter {

    override fun startProcessingEvent(event: Event) {
        addProperty("Event", "${event.name}:V${event.version}")
        addProperty("EventID", event.id)
        addProperty("FlowID", event.flowId)
        addProperty("UserID", event.identity.get("userId").asString ?: "unknown")
        addProperty("Origin", event.metadata.get("origin").asString ?: "unknown")
    }

    override fun eventProcessFinished(event: Event) {
        MDC.clear()
    }

    override fun addProperty(key: String, value: String) {
        MDC.put(key, value)
    }

    override fun notifyError(exception: Exception) {
    }

}