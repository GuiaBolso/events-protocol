package br.com.guiabolso.tracing.builder

import br.com.guiabolso.tracing.TracerImpl
import br.com.guiabolso.tracing.context.SimpleThreadContextManager
import br.com.guiabolso.tracing.engine.datadog.DatadogStatsDTracer
import br.com.guiabolso.tracing.engine.datadog.DatadogTracer
import br.com.guiabolso.tracing.engine.slf4j.Slf4JTracer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TracerBuilderTest {

    @Test
    fun `should create a tracer with Slf4`() {
        val tracer = TracerBuilder().withSlf4J().build()

        tracer as TracerImpl

        assertEquals(1, tracer.engines.size)
        assertTrue(tracer.engines.first() is Slf4JTracer)

        assertEquals(1, tracer.contextManagers.size)
        assertTrue(tracer.contextManagers.first() is Slf4JTracer)
    }

    @Test
    fun `should create a tracer with datadog APM`() {
        val tracer = TracerBuilder().withDatadogAPM().build()

        tracer as TracerImpl

        assertEquals(1, tracer.engines.size)
        assertTrue(tracer.engines.first() is DatadogTracer)

        assertEquals(1, tracer.contextManagers.size)
        assertTrue(tracer.contextManagers.first() is DatadogTracer)
    }

    @Test
    fun `should create a tracer with datadog APM with statsD`() {
        val tracer = TracerBuilder().withDatadogAPMAndStatsD("prefix", "localhost", 8080).build()

        tracer as TracerImpl

        assertEquals(1, tracer.engines.size)
        assertTrue(tracer.engines.first() is DatadogStatsDTracer)

        assertEquals(1, tracer.contextManagers.size)
        assertTrue(tracer.contextManagers.first() is DatadogStatsDTracer)

        val statsDTracer = tracer.engines.first() as DatadogStatsDTracer
        assertEquals("prefix", statsDTracer.prefix)
        assertEquals("localhost", statsDTracer.host)
        assertEquals(8080, statsDTracer.port)

        statsDTracer.close()
    }

    @Test
    fun `should create a tracer with the SimpleThreadContextManager`() {
        val tracer = TracerBuilder().withContextManager(SimpleThreadContextManager()).build()

        tracer as TracerImpl

        assertEquals(0, tracer.engines.size)

        assertEquals(1, tracer.contextManagers.size)
        assertTrue(tracer.contextManagers.first() is SimpleThreadContextManager)
    }
}
