package br.com.guiabolso.tracing.engine.datadog

import br.com.guiabolso.tracing.engine.MetricReporterEngine
import br.com.guiabolso.tracing.utils.ExceptionUtils
import datadog.trace.api.DDTags.ERROR_MSG
import datadog.trace.api.DDTags.ERROR_STACK
import datadog.trace.api.DDTags.ERROR_TYPE
import datadog.trace.api.DDTags.RESOURCE_NAME
import datadog.trace.api.DDTags.THREAD_ID
import datadog.trace.api.DDTags.THREAD_NAME
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import java.io.Closeable


class DatadogMetricReporter : MetricReporterEngine<Span> {

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

    override fun extractContext(): Span {
        val scope = tracer.buildSpan("asyncTask").startActive(false)
        return scope.span()
    }

    override fun withContext(context: Any): Closeable {
        val scope = tracer.scopeManager().activate(context as Span, true)

        addProperty(THREAD_ID, Thread.currentThread().id)
        addProperty(THREAD_NAME, Thread.currentThread().name)

        return scope
    }

    override fun withContext(context: Span, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {}

    private val tracer: Tracer
        get() = GlobalTracer.get()

}