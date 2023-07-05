package br.com.guiabolso.tracing.engine.opentelemetry

import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.utils.opentelemetry.OpenTelemetryUtils
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.LongHistogram
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context
import java.io.Closeable
import java.util.concurrent.ConcurrentHashMap

class OpenTelemetryTracer : TracerEngine, ThreadContextManager<Span> {

    override val type = Span::class.java

    private val tracer: Tracer by lazy {
        GlobalOpenTelemetry.getTracer(TRACER_NAME)
    }

    private val meter: Meter by lazy {
        GlobalOpenTelemetry.getMeter(TRACER_NAME)
    }

    override fun setOperationName(name: String) {
        val span = getRootSpan()
        span?.updateName(name)
    }

    override fun addProperty(key: String, value: String?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: String?) {
        getRootSpan()?.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Number?) {
        getRootSpan()?.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        getRootSpan()?.addProperty(key, value)
    }

    override fun addProperty(key: String, value: List<*>) {
        val finalValue: String = value.joinToString(",")
        addProperty(key, finalValue)
    }

    override fun <T> recordExecutionTime(name: String, block: (MutableMap<String, String>) -> T): T {
        val start = System.currentTimeMillis()
        val context = mutableMapOf<String, String>()
        try {
            return block(context)
        } finally {
            val elapsedTime = System.currentTimeMillis() - start
            recordExecutionTime(name, elapsedTime, context)
        }
    }

    override fun recordExecutionTime(name: String, elapsedTime: Long, context: Map<String, String>) {
        val attributes = Attributes.builder()
        for ((k, v) in context) {
            attributes.put(k, v)
        }
        val lh = histogramCache.computeIfAbsent(name) {
            meter.histogramBuilder(name).setUnit("ms").ofLongs().build()
        }
        lh.record(elapsedTime, attributes.build())
    }

    override fun notifyError(exception: Throwable, expected: Boolean) {
        Span.current()?.let { span ->
            OpenTelemetryUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyRootError(exception: Throwable, expected: Boolean) {
        getRootSpan()?.let { span ->
            OpenTelemetryUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        Span.current()?.let { span ->
            OpenTelemetryUtils.notifyError(span, message, params, expected)
        }
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        getRootSpan()?.let { span ->
            OpenTelemetryUtils.notifyError(span, message, params, expected)
        }
    }

    override fun clear() {}

    override fun extract(): Span {
        return Span.current()
    }

    override fun withContext(context: Span): Closeable {
        val span = tracer.spanBuilder("asyncTask").setParent(Context.current().with(context)).startSpan()
        val scope = span.makeCurrent()
        return Closeable {
            span.end()
            scope.close()
        }
    }

    private inline fun <reified T> Span.addProperty(key: String, value: T?) {
        val attrKey = getAttributeKey<T>(key)
        if (value != null) {
            this.setAttribute(attrKey, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> getAttributeKey(key: String): AttributeKey<T> {
        return keysMap.computeIfAbsent(key) { k: String ->
            val tClass = T::class.java
            when {
                String::class.java.isAssignableFrom(tClass) -> AttributeKey.stringKey(k)
                Double::class.java.isAssignableFrom(tClass) -> AttributeKey.doubleKey(k)
                Number::class.java.isAssignableFrom(tClass) -> AttributeKey.doubleKey(k)
                Int::class.java.isAssignableFrom(tClass) -> AttributeKey.longKey(k)
                Long::class.java.isAssignableFrom(tClass) -> AttributeKey.longKey(k)
                Boolean::class.java.isAssignableFrom(tClass) -> AttributeKey.booleanKey(k)
                else -> error("Unsupported attribute type ${tClass.canonicalName}")
            }
        } as AttributeKey<T>
    }

    private fun getRootSpan(): Span? = Context.root().get(SpanContextKey.KEY)

    companion object {
        const val TRACER_NAME = "events-tracing"
        private val keysMap = ConcurrentHashMap<String, AttributeKey<*>>()
        private val histogramCache = ConcurrentHashMap<String, LongHistogram>()
    }
}
