package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event
import com.newrelic.api.agent.NewRelic

class NewrelicMetricReporter : MetricReporter {

    override fun startProcessingEvent(event: Event) {
        NewRelic.setTransactionName("EventProcessor", "${event.name}:V${event.version}")
        NewRelic.addCustomParameter("EventID", event.id)
        NewRelic.addCustomParameter("FlowID", event.flowId)
        NewRelic.addCustomParameter("UserID", event.identity.get("userId").asString ?: "unknown")
        NewRelic.addCustomParameter("Origin", event.metadata.get("origin").asString ?: "unknown")
    }

    override fun eventProcessFinished(event: Event) {
    }

    override fun addProperty(key: String, value: String) {
        NewRelic.addCustomParameter(key, value)
    }

    override fun notifyError(exception: Exception) {
        NewRelic.noticeError(exception)
    }

}