package br.com.guiabolso.events.tracer

import br.com.guiabolso.events.context.EventThreadContextManager
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.builder.TracerBuilder

object DefaultTracer : Tracer
by TracerBuilder()
    .withDatadogAPM()
    .withSlf4()
    .withContextManager(EventThreadContextManager)
    .build()
