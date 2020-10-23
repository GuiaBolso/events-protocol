package br.com.guiabolso.events.client

import br.com.guiabolso.events.client.model.Response
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.tracer.propagation.EventFormat
import br.com.guiabolso.events.tracer.propagation.EventTextMapAdapter
import io.opentracing.Tracer

class TracingEventClient(
    private val inner: EventClient,
    private val tracer: Tracer
) {

    @JvmOverloads
    fun sendEvent(
        url: String,
        requestEvent: RequestEvent,
        headers: Map<String, String> = emptyMap(),
        timeout: Int? = null
    ): Response {
        val activeSpan = tracer.activeSpan()
        if (activeSpan != null) {
            tracer.inject(activeSpan.context(), EventFormat, EventTextMapAdapter(requestEvent))
        }
        return inner.sendEvent(url, requestEvent, headers, timeout)
    }
}
