package br.com.guiabolso.events.server

import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import kotlinx.coroutines.runBlocking

class BlockingEventProcessor(
    private val eventProcessor: SuspendingEventProcessor
) {
    @JvmOverloads
    constructor(
        discovery: EventHandlerDiscovery,
        exceptionHandlerRegistry: ExceptionHandlerRegistry,
        tracer: Tracer = DefaultTracer,
        eventValidator: EventValidator = StrictEventValidator()
    ) : this(SuspendingEventProcessor(discovery, exceptionHandlerRegistry, tracer, eventValidator))

    fun processEvent(payload: String?): String = runBlocking {
        eventProcessor.processEvent(payload)
    }
}
