package br.com.guiabolso.tracing.builder

import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.TracerImpl
import br.com.guiabolso.tracing.context.ThreadContextManager
import br.com.guiabolso.tracing.engine.TracerEngine
import br.com.guiabolso.tracing.engine.datadog.DatadogStatsDTracer
import br.com.guiabolso.tracing.engine.datadog.DatadogTracer
import br.com.guiabolso.tracing.engine.slf4j.Slf4JTracer

class TracerBuilder {

    private var tracerEngines = mutableListOf<TracerEngine>()
    private val contextManagers = mutableListOf<ThreadContextManager<*>>()

    fun withSlf4J(): TracerBuilder {
        withEngine(Slf4JTracer())
        return this
    }

    fun withDatadogAPM(): TracerBuilder {
        withEngine(DatadogTracer())
        return this
    }

    fun withDatadogAPMAndStatsD(prefix: String, host: String, port: Int): TracerBuilder {
        withEngine(DatadogStatsDTracer(prefix, host, port))
        return this
    }

    fun withEngine(engine: TracerEngine): TracerBuilder {
        tracerEngines.add(engine)
        if (engine is ThreadContextManager<*>) {
            withContextManager(engine)
        }
        return this
    }

    fun withContextManager(manager: ThreadContextManager<*>): TracerBuilder {
        contextManagers.add(manager)
        return this
    }

    fun build(): Tracer {
        return TracerImpl(tracerEngines, contextManagers)
    }
}
