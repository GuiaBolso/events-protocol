package br.com.guiabolso.events.metric

import br.com.guiabolso.events.model.Event
import com.newrelic.api.agent.NewRelic

class NewRelicMetricReporter : MetricReporter {

    override fun startProcessingEvent(event: Event) {
        NewRelic.setTransactionName("EventProcessor", "${event.name}:V${event.version}")
        NewRelic.addCustomParameter("EventID", event.id)
        NewRelic.addCustomParameter("FlowID", event.flowId)
        NewRelic.addCustomParameter("UserID", event.userId?.toString() ?: "Unknown")
        NewRelic.addCustomParameter("Origin", event.origin)
    }

    override fun eventProcessFinished(event: Event) {
    }

    override fun addProperty(key: String, value: String) {
        NewRelic.addCustomParameter(key, value)
    }

    override fun addProperty(key: String, value: Number) {
        NewRelic.addCustomParameter(key, value)
    }

    override fun notifyError(exception: Throwable) {
        NewRelic.noticeError(exception)
    }

}