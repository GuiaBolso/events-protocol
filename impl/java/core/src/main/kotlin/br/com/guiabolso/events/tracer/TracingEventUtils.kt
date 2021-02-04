package br.com.guiabolso.events.tracer

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.tracer.propagation.EventFormat
import br.com.guiabolso.events.tracer.propagation.EventTextMapAdapter
import br.com.guiabolso.tracing.utils.ExceptionUtils
import datadog.trace.api.DDTags
import io.opentracing.Span
import io.opentracing.SpanContext
import io.opentracing.Tracer
import io.opentracing.tag.Tags

suspend fun Tracer.traceEvent(
    operationName: String,
    event: RequestEvent,
    func: suspend (Span) -> ResponseEvent,
): ResponseEvent {
    val span = newSpan(operationName, event)
    return this.activateSpan(span).use {
        try {
            func(span)
        } catch (e: Exception) {
            notifyError(span, e)
            throw e
        } finally {
            span.finish()
        }
    }
}

fun <R> Tracer.trace(operationName: String, func: (Span) -> R): R {
    val span = newSpan(operationName)
    return this.activateSpan(span).use {
        try {
            func(span)
        } catch (e: Exception) {
            notifyError(span, e)
            throw e
        } finally {
            span.finish()
        }
    }
}

private fun Tracer.newSpan(operationName: String, requestEvent: RequestEvent? = null): Span {
    val spanContext = getContext(requestEvent)

    val spanBuilder = this.buildSpan(operationName)
    if (spanContext != null) {
        spanBuilder.asChildOf(spanContext)
    } else {
        spanBuilder.ignoreActiveSpan()
    }
    return spanBuilder.start()
}

private fun Tracer.getContext(requestEvent: RequestEvent?): SpanContext? = requestEvent?.run {
    this@getContext.extract(EventFormat, EventTextMapAdapter(this))
} ?: this@getContext.activeSpan()?.context()

private fun notifyError(span: Span, e: Exception): Span? {
    span.setTag(Tags.ERROR.key, true)
    span.setTag(DDTags.ERROR_MSG, e.message ?: "Empty message")
    span.setTag(DDTags.ERROR_TYPE, e.javaClass.name)
    return span.setTag(DDTags.ERROR_STACK, ExceptionUtils.getStackTrace(e))
}
