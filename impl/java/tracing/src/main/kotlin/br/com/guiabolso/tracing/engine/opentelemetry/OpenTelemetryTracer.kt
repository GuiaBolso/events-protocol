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

@Suppress("TooManyFunctions")
class OpenTelemetryTracer : TracerEngine, ThreadContextManager<Span> {

    override val type = Span::class.java

    private val tracer: Tracer by lazy {
        GlobalOpenTelemetry.getTracer(TRACER_NAME)
    }

    private val meter: Meter by lazy {
        GlobalOpenTelemetry.getMeter(TRACER_NAME)
    }

    override fun setOperationName(name: String) {
        val span = currentSpan()
        span?.updateName(name)
    }

    override fun addProperty(key: String, value: String?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: String?) {
        currentSpan()?.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Number?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Number?) {
        currentSpan()?.addProperty(key, value)
    }

    override fun addProperty(key: String, value: Boolean?) {
        Span.current()?.addProperty(key, value)
    }

    override fun addRootProperty(key: String, value: Boolean?) {
        currentSpan()?.addProperty(key, value)
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
        currentSpan()?.let { span ->
            OpenTelemetryUtils.notifyError(span, exception, expected)
        }
    }

    override fun notifyError(message: String, params: Map<String, String?>, expected: Boolean) {
        Span.current()?.let { span ->
            OpenTelemetryUtils.notifyError(span, message, params, expected)
        }
    }

    override fun notifyRootError(message: String, params: Map<String, String?>, expected: Boolean) {
        currentSpan()?.let { span ->
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
        val attributeSetter = getAttributeSetterOf<T, Any>(key)
        if (value != null) {
            attributeSetter.setAttributeIn(this, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T, R> getAttributeSetterOf(key: String): Setter<T, R> {
        return keysMap.computeIfAbsent(key) { k: String ->
            val tClass = T::class.java
            resolveAttributeSetterFor<T>(tClass, k)
        } as Setter<T, R>
    }

    private inline fun <reified T> resolveAttributeSetterFor(tClass: Class<T>, k: String) = when {
        String::class.java.isAssignableFrom(tClass) -> Setter<String, String>(AttributeKey.stringKey(k)) { it }
        Double::class.java.isAssignableFrom(tClass) -> Setter<Double, Double>(AttributeKey.doubleKey(k)) { it }
        Float::class.java.isAssignableFrom(tClass) -> Setter<Float, Double>(AttributeKey.doubleKey(k)) { it.toDouble() }
        Int::class.java.isAssignableFrom(tClass) -> Setter<Int, Long>(AttributeKey.longKey(k)) { it.toLong() }
        Long::class.java.isAssignableFrom(tClass) -> Setter<Long, Long>(AttributeKey.longKey(k)) { it }
        Number::class.java.isAssignableFrom(tClass) -> handleJavaTypes(k, tClass)
        Boolean::class.java.isAssignableFrom(tClass) -> Setter<Boolean, Boolean>(AttributeKey.booleanKey(k)) { it }
        else -> error("Unsupported attribute type ${tClass.canonicalName}")
    }

    private fun <T> handleJavaTypes(k: String, tClass: Class<T>): Setter<Number, out Number> {
        return when {
            java.lang.Long::class.java.isAssignableFrom(tClass) || Integer::class.java.isAssignableFrom(tClass) -> {
                Setter<Number, Long>(AttributeKey.longKey(k)) { it.toLong() }
            }

            else -> Setter<Number, Double>(AttributeKey.doubleKey(k)) { it.toDouble() }
        }
    }

    private fun currentSpan(): Span? = Span.current()

    private class Setter<T, R>(private val key: AttributeKey<R>, private val transformer: (T) -> R) {
        fun setAttributeIn(span: Span, value: T) {
            span.setAttribute(key, transformer(value))
        }
    }

    companion object {
        const val TRACER_NAME = "events-tracing"
        private val keysMap = ConcurrentHashMap<String, Setter<*, *>>()
        private val histogramCache = ConcurrentHashMap<String, LongHistogram>()
    }
}
