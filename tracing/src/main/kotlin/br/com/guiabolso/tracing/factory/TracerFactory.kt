package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.TracerImpl
import br.com.guiabolso.tracing.async.DefaultAsyncExecutor
import br.com.guiabolso.tracing.async.NewRelicAsyncExecutor
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.engine.datadog.DatadogTracer
import br.com.guiabolso.tracing.engine.newrelic.NewRelicTracer
import br.com.guiabolso.tracing.engine.slf4j.Slf4JTracer
import br.com.guiabolso.tracing.utils.ClassPathUtils.isDatadogPresent
import br.com.guiabolso.tracing.utils.ClassPathUtils.isNewRelicPresent
import org.slf4j.LoggerFactory

object TracerFactory {

    @JvmStatic
    private val LOGGER = LoggerFactory.getLogger(TracerFactory::class.java)

    @JvmStatic
    fun createTracer(): Tracer = when {
        isDatadogPresent() -> createTracerWithDatadog()
        isNewRelicPresent() -> createTracerWithNewRelic()
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
    fun createTracerWithNewRelic(): Tracer {
        LOGGER.info("Using NewRelic as APM tracer.")
        return TracerImpl(
                compose(Slf4JTracer(), NewRelicTracer()),
                NewRelicAsyncExecutor()
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