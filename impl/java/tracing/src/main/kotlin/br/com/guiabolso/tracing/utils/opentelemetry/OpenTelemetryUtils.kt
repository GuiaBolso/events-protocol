package br.com.guiabolso.tracing.utils.opentelemetry

import br.com.guiabolso.tracing.engine.opentelemetry.OpenTelemetryTracer.Companion.TRACER_NAME
import br.com.guiabolso.tracing.utils.opentelemetry.coroutine.asContextElement
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object OpenTelemetryUtils {

    private val tracer: Tracer by lazy {
        GlobalOpenTelemetry.getTracer(TRACER_NAME)
    }

    @JvmStatic
    @JvmOverloads
    fun traceAsNewOperation(
        name: String,
        kind: SpanKind = SpanKind.SERVER,
        func: () -> Unit
    ) = runBlocking {
        coTraceAsNewOperation(name, kind, func)
    }

    suspend fun coTraceAsNewOperation(
        name: String,
        kind: SpanKind = SpanKind.SERVER,
        func: suspend () -> Unit
    ) {
        val span = tracer.spanBuilder(name)
            .setSpanKind(kind)
            .setNoParent()
            .startSpan()!!
        withContext(span.asContextElement()) {
            try {
                func()
            } catch (e: Exception) {
                notifyError(span, e, false)
                throw e
            } finally {
                span.end()
            }
        }
    }

    @JvmStatic
    fun <T> traceBlock(name: String, func: () -> T): T = runBlocking {
        suspendingTraceBlock(name) { func() }
    }

    suspend fun <T> suspendingTraceBlock(name: String, func: suspend () -> T): T {
        val span = tracer.spanBuilder(name).startSpan()!!
        return withContext(span.asContextElement()) {
            try {
                func()
            } catch (e: Exception) {
                notifyError(span, e, false)
                throw e
            } finally {
                span.end()
            }
        }
    }

    @JvmStatic
    fun notifyError(span: Span, exception: Throwable, expected: Boolean) {
        val status = if (expected) StatusCode.OK else StatusCode.ERROR
        span.setStatus(status)
        span.recordException(exception)
    }

    @JvmStatic
    fun notifyError(span: Span, message: String, params: Map<String, String?>, expected: Boolean) {
        val status = if (expected) StatusCode.OK else StatusCode.ERROR
        span.setStatus(status)
        val builder = Attributes.builder()
        params.forEach { (key, value) ->
            if (value != null) {
                builder.put(key, value)
            }
        }
        span.recordException(DefaultUnspecifiedException(message), builder.build())
    }
}
