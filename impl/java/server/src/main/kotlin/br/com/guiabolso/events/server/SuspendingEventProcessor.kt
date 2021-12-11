package br.com.guiabolso.events.server

import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventParsingException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import java.util.UUID

class SuspendingEventProcessor(private val processor: RawEventProcessor) {

    @JvmOverloads
    constructor(
        discovery: EventHandlerDiscovery,
        exceptionHandlerRegistry: ExceptionHandlerRegistry,
        tracer: Tracer = DefaultTracer,
        eventValidator: EventValidator = StrictEventValidator(),
        traceOperationPrefix: String = ""
    ) : this(RawEventProcessor(discovery, exceptionHandlerRegistry, tracer, eventValidator, traceOperationPrefix))

    suspend fun processEvent(payload: String?): String {
        val rawEvent = try {
            parseEvent(payload)
        } catch (e: EventParsingException) {
            return processor.exceptionHandlerRegistry.handleException(e, badProtocol(), processor.tracer).json()
        }
        return processor.processEvent(rawEvent).json()
    }

    private fun parseEvent(payload: String?): RawEvent {
        return try {
            mapper.fromJson(payload ?: throw EventParsingException(null), RawEvent::class.java)
        } catch (e: Throwable) {
            throw EventParsingException(e)
        } ?: throw EventParsingException(null)
    }

    private fun badProtocol() = RequestEvent(
        name = BadProtocol.typeName,
        version = 1,
        id = UUID.randomUUID().toString(),
        flowId = UUID.randomUUID().toString(),
        payload = TreeNode(),
        identity = TreeNode(),
        auth = TreeNode(),
        metadata = TreeNode()
    )

    private fun ResponseEvent.json() = mapper.toJson(this)
}
