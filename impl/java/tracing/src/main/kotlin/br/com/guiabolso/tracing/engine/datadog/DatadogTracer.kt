package br.com.guiabolso.tracing.engine.datadog

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.utils.DatadogUtils
import datadog.trace.api.DDTags.RESOURCE_NAME
import datadog.trace.api.interceptor.MutableSpan
import io.opentracing.Span
import io.opentracing.Tracer
import io.opentracing.util.GlobalTracer
import java.io.Closeable

@Suppress("TooManyFunctions")
open class DatadogTracer : TracerEngine, ThreadContextManager<Span> {

    override val type = Span::class.java

    private val tracer: Tracer
        get() = GlobalTracer.get()

    override fun setOperationName(name: String) {
        addProperty(RESOURCE_NAME, name)
    }

    override fun addProperty(key: String, value: String?) {
        tracer.activeSpan()?.setTag(key, value)
    }

    override fun addRootProperty(key: String, value: String?) {
        tracer.activeSpan()?.let { span ->
            if (span is MutableSpan) span.localRootSpan?.run { this.setTag(key, value) as Any }
            else span.setTag(key, value)
        }
    }

    override fun addProperty(key: String, value: Number?) {
        tracer.activeSpan()?.setTag(key, value)
    }

    override fun addRootProperty(key: String, value: Number?) {
        tracer.activeSpan()?.let { span ->
            if (span is MutableSpan) span.localRootSpan?.run { this.setTag(key, value) as Any }
            else span.setTag(key, value)
        }
    }

    override fun addProperty(key: String, value: Boolean?) {
        if (value != null) tracer.activeSpan()?.setTag(key, value)
    }

    @Suppress("NestedBlockDepth")
    override fun addRootProperty(key: String, value: Boolean?) {
        if (value != null) {
            tracer.activeSpan()?.let { span ->
                if (span is MutableSpan) span.localRootSpan?.run { this.setTag(key, value) as Any }
                else span.setTag(key, value)
            }
        }
    }

    override fun addProperty(key: String, value: List<*>) {
        val finalValue: String = value.joinToString(",")
        addProperty(key, finalValue)
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        throw NotImplementedError("Import com.datadoghq:java-dogstatsd-client dependency to use this feature.")
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: Map<String, String>) {
        throw NotImplementedError("Import com.datadoghq:java-dogstatsd-client dependency to use this feature.")
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        val span = tracer.activeSpan()
        if (span != null) {
            DatadogUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        tracer.activeSpan()?.let { span ->
            if (span is MutableSpan) span.localRootSpan.isError = !expected
            DatadogUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        val span = tracer.activeSpan()
        if (span != null) {
            DatadogUtils.notifyError(span, message, params, expected)
        }
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        tracer.activeSpan()?.let { span ->
            if (span is MutableSpan) span.localRootSpan.isError = !expected
            DatadogUtils.notifyError(span, message, params, expected)
        }
    }

    override fun clear() {}

    override fun extract(): Span {
        return tracer.activeSpan()
    }

    override fun withContext(context: Span): Closeable {
        val span = tracer.buildSpan("asyncTask").asChildOf(context).start()
        val scope = tracer.activateSpan(span)
        return Closeable {
            span.finish()
            scope.close()
        }
    }
}
