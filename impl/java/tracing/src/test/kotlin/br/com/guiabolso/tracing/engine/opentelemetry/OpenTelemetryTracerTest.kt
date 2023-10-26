package br.com.guiabolso.tracing.engine.opentelemetry

import br.com.guiabolso.tracing.engine.opentelemetry.OpenTelemetryTracer.Companion.TRACER_NAME
import br.com.guiabolso.tracing.utils.opentelemetry.DefaultUnspecifiedException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
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
    fun `should update the span name when set operation`() {
        mockkStatic(LocalRootSpan::current)
        val span = mockk<Span>(relaxed = true)
        every { LocalRootSpan.current() } returns span

        openTelemetryTracer.setOperationName("my-operation")

        verify {
            span.updateName("my-operation")
        }
    }

    @Test
    fun `should add property to root span`() {
        mockkStatic(LocalRootSpan::current)
        val span = mockk<Span>(relaxed = true)
        every { LocalRootSpan.current() } returns span

        openTelemetryTracer.addRootProperty("number", 1)
        openTelemetryTracer.addRootProperty("bool", true)
        openTelemetryTracer.addRootProperty("string", "my-string")

        verify(exactly = 1) {
            span.setAttribute(AttributeKey.longKey("number"), 1L)
            span.setAttribute(AttributeKey.booleanKey("bool"), true)
            span.setAttribute(AttributeKey.stringKey("string"), "my-string")
        }
    }

    @Test
    fun `should report error on root span`() {
        mockkStatic(LocalRootSpan::current)
        val span = mockk<Span>(relaxed = true)
        every { LocalRootSpan.current() } returns span

        val ex = NotImplementedError()
        openTelemetryTracer.notifyRootError(ex, expected = false)
        openTelemetryTracer.notifyRootError("my error", mapOf("tag" to "1"), expected = false)

        verify(exactly = 2) {
            span.setStatus(StatusCode.ERROR)
        }

        verify {
            span.recordException(ex)
            span.recordException(
                withArg {
                    assertInstanceOf(DefaultUnspecifiedException::class.java, it)
                    assertEquals("my error", it.message)
                },
                Attributes.builder().put("tag", "1").build()
            )
        }
    }

    private fun currentSpyiedSpan(): Span {
        return otel.getTracer(TRACER_NAME)
            .spanBuilder("name")
            .setNoParent()
            .startSpan()
            .run { spyk(this) }
            .also { it.makeCurrent() }
    }
}
