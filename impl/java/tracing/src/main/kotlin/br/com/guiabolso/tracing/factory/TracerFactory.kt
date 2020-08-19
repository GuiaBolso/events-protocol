package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.TracerImpl
import br.com.guiabolso.tracing.async.DefaultAsyncExecutor
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.engine.datadog.DatadogStatsDTracer
import br.com.guiabolso.tracing.engine.datadog.DatadogTracer
import br.com.guiabolso.tracing.engine.slf4j.Slf4JTracer
import br.com.guiabolso.tracing.utils.ClassPathUtils.isDatadogPresent
import br.com.guiabolso.tracing.utils.ClassPathUtils.isStatsDPresent
import br.com.guiabolso.tracing.utils.EnvironmentUtils
import org.slf4j.LoggerFactory

object TracerFactory {

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(TracerFactory::class.java)

    @JvmStatic
    fun createTracer(): Tracer = when {
        isDatadogPresent() -> {
            if (isStatsDPresent()) createTracerWithDatadogStatsD() else createTracerWithDatadog()
        }
        else -> createTracerWithoutAnyAPM()
    }

    @JvmStatic
    fun createTracerWithDatadog(): Tracer {
        LOGGER.info("Using Datadog as APM tracer.")
        return TracerImpl(
            compose(Slf4JTracer(), DatadogTracer()),
            DefaultAsyncExecutor()
        )
    }

    @JvmStatic
    fun createTracerWithDatadogStatsD(): Tracer {
        LOGGER.info("Using Datadog as APM tracer with StatsD.")
        return TracerImpl(
            compose(
                Slf4JTracer(),
                DatadogStatsDTracer(
                    EnvironmentUtils.getProperty("DD_SERVICE_NAME", "unknown-application"),
                    EnvironmentUtils.getProperty("DD_AGENT_HOST", "localhost"),
                    EnvironmentUtils.getProperty("DD_AGENT_PORT", 8125)
                )
            ),
            DefaultAsyncExecutor()
        )
    }

    @JvmStatic
    fun createTracerWithoutAnyAPM(): Tracer {
        LOGGER.info("No APM tracer detected.")
        return TracerImpl(
            Slf4JTracer(),
            DefaultAsyncExecutor()
        )
    }

    @JvmStatic
    private fun compose(vararg tracers: TracerEngine<*>) = CompositeTracerEngine(tracers.toList())
}
