package br.com.guiabolso.tracing.engine.datadog

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import br.com.guiabolso.tracing.utils.ExceptionUtils
import datadog.trace.api.DDTags.*
import io.opentracing.Tracer
import io.opentracing.Tracer.SpanBuilder
import io.opentracing.util.GlobalTracer
import java.io.Closeable


class DatadogMetricReporter : MetricReporterEngine<SpanBuilder> {

    override fun setOperationName(name: String) {
        addProperty(RESOURCE_NAME, name)
    }

    override fun addProperty(key: String, value: String?) {
        tracer.activeSpan()?.setTag(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        tracer.activeSpan()?.setTag(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        if (value != null) tracer.activeSpan()?.setTag(key, value)
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        addProperty("error", (!expected).toString())
        addProperty(ERROR_MSG, exception.message ?: "Empty message")
        addProperty(ERROR_TYPE, exception.javaClass.name)
        addProperty(ERROR_STACK, ExceptionUtils.getStackTrace(exception))
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        addProperty("error", (!expected).toString())
        addProperty(ERROR_MSG, message)
        params.forEach { k, v -> addProperty(k, v) }
    }

    override fun extractContext(): SpanBuilder {
        return tracer.buildSpan("asyncTask")
                .asChildOf(tracer.activeSpan())
    }

    override fun withContext(context: Any): Closeable {
        return (context as SpanBuilder).startActive(true)
    }

    override fun withContext(context: SpanBuilder, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {}

    private val tracer: Tracer
        get() = GlobalTracer.get()

}