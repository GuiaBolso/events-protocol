package br.com.guiabolso.tracing.engine.opentelemetry

import io.mockk.spyk
import io.mockk.verify
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.trace.Span
import org.junit.jupiter.api.Test

private val otel = OpenTelemetry.noop().apply(GlobalOpenTelemetry::set)

class OpenTelemetryTracerTest {
    private val openTelemetryTracer = OpenTelemetryTracer()

    @Test
    fun `should add all supported properties successfully`() {
        val span = currentSpyiedSpan()
        openTelemetryTracer.run {
            addProperty("boolean", true)
            addProperty("string", "Hello World")
            addProperty("byte", Byte.MAX_VALUE)
            addProperty("short", Short.MAX_VALUE)
            addProperty("int", Int.MAX_VALUE)
            addProperty("float", Float.MAX_VALUE)
            addProperty("long", Long.MAX_VALUE)
            addProperty("double", Double.MAX_VALUE)
            addProperty("list", listOf("a", 1, "b"))
        }
        verify {
            span.setAttribute(AttributeKey.booleanKey("boolean"), true)
            span.setAttribute(AttributeKey.stringKey("string"), "Hello World")
            span.setAttribute(AttributeKey.longKey("byte"), Byte.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey("short"), Short.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey("int"), Int.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey("long"), Long.MAX_VALUE)
            span.setAttribute(AttributeKey.doubleKey("float"), Float.MAX_VALUE.toDouble())
            span.setAttribute(AttributeKey.doubleKey("double"), Double.MAX_VALUE)
            span.setAttribute(AttributeKey.stringKey("list"), "a,1,b")
        }
    }

    @Test
    fun `allow the same key to be reported by multiple datatype`() {
        val span = currentSpyiedSpan()
        val key = "my.attribute.name"
        openTelemetryTracer.run {
            addProperty(key, true)
            addProperty(key, "Hello World")
            addProperty(key, Byte.MAX_VALUE)
            addProperty(key, Short.MAX_VALUE)
            addProperty(key, Int.MAX_VALUE)
            addProperty(key, Float.MAX_VALUE)
            addProperty(key, Long.MAX_VALUE)
            addProperty(key, Double.MAX_VALUE)
            addProperty(key, listOf("a", 1, "b"))
        }
        verify {
            span.setAttribute(AttributeKey.booleanKey(key), true)
            span.setAttribute(AttributeKey.stringKey(key), "Hello World")
            span.setAttribute(AttributeKey.longKey(key), Byte.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey(key), Short.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey(key), Int.MAX_VALUE.toLong())
            span.setAttribute(AttributeKey.longKey(key), Long.MAX_VALUE)
            span.setAttribute(AttributeKey.doubleKey(key), Float.MAX_VALUE.toDouble())
            span.setAttribute(AttributeKey.doubleKey(key), Double.MAX_VALUE)
            span.setAttribute(AttributeKey.stringKey(key), "a,1,b")
        }
    }

    @Test
    fun `should set operation name successfully`() {
        openTelemetryTracer.setOperationName("my-operation")
    }

    private fun currentSpyiedSpan(): Span {
        return otel.getTracer(OpenTelemetryTracer.TRACER_NAME)
            .spanBuilder("name")
            .setNoParent()
            .startSpan()
            .run { spyk(this) }
            .also { it.makeCurrent() }
    }
}
