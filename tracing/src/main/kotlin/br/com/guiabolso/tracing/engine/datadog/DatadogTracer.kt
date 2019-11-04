package br.com.guiabolso.tracing.engine.datadog

import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.utils.DatadogUtils
import datadog.trace.api.DDTags.RESOURCE_NAME
import io.opentracing.Tracer
import io.opentracing.Tracer.SpanBuilder
import io.opentracing.util.GlobalTracer
import java.io.Closeable


open class DatadogTracer : TracerEngine<SpanBuilder> {
    private val tracer: Tracer
        get() = GlobalTracer.get()

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

    override fun addProperty(key: String, value: List<*>) {
        val finalValue:String = value.joinToString(",")
        addProperty(key, finalValue)
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: MutableMap<String, String>) {
        throw NotImplementedError("Import com.datadoghq:java-dogstatsd-client dependency to use this feature.")
    }

    override fun <T> executeAndRecordTime(name: String, block: (MutableMap<String, String>) -> T): T {
        throw NotImplementedError("Import com.datadoghq:java-dogstatsd-client dependency to use this feature.")
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        val span = tracer.activeSpan()
        if (span != null) {
            DatadogUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        val span = tracer.activeSpan()
        if (span != null) {
            DatadogUtils.notifyError(span, message, params, expected)
        }
    }

    override fun extractContext(): SpanBuilder {
        return tracer.buildSpan("asyncTask")
            .asChildOf(tracer.activeSpan())
    }

    override fun withContext(context: Any): Closeable {
        return tracer.scopeManager().activate((context as SpanBuilder).start())
    }

    override fun withContext(context: SpanBuilder, func: () -> Any) {
        withContext(context).use { func() }
    }

    override fun clear() {}

}