package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.server.parser.EventParsingException
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.factory.TracerFactory

class EventProcessor
@JvmOverloads
constructor(
    discovery: EventHandlerDiscovery,
    exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tracer: Tracer = TracerFactory.createTracer(),
    eventValidator: EventValidator = StrictEventValidator()
) {

    private val eventProcessor = RawEventProcessor(discovery, exceptionHandlerRegistry, tracer, eventValidator)

    fun processEvent(payload: String?): String {
        return try {
            val rawEvent = parseEvent(payload)
            eventProcessor.processEvent(rawEvent).json()
        } catch (e: EventParsingException) {
            tracer.notifyError(e, false)
            badProtocol(e.eventMessage).json()
        }
    }

    private fun parseEvent(payload: String?): RawEvent? {
        try {
            return MapperHolder.mapper.fromJson(payload, RawEvent::class.java)
        } catch (e: Exception) {
            throw EventParsingException(e)
        }
    }

    private fun ResponseEvent.json() = MapperHolder.mapper.toJson(this)

}