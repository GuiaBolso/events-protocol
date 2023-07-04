package br.com.guiabolso.events.server

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventParsingException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import java.util.UUID

class SuspendingEventProcessor(
    private val processor: RawEventProcessor,
    private val jsonAdapter: JsonAdapter,
) {

    @JvmOverloads
    constructor(
        discovery: EventHandlerDiscovery,
        exceptionHandlerRegistry: ExceptionHandlerRegistry,
        tracer: Tracer = DefaultTracer,
        eventValidator: EventValidator = StrictEventValidator(),
        traceOperationPrefix: String = "",
        jsonAdapter: JsonAdapter,
    ) : this(
        RawEventProcessor(
            discovery,
            exceptionHandlerRegistry,
            tracer,
            eventValidator,
            traceOperationPrefix,
            jsonAdapter
        ),
        jsonAdapter
    )

    suspend fun processEvent(payload: String?): String {
        return try {
            processor.processEvent(
                rawEvent = parseEvent(payload)
            ).json()
        } catch (e: EventParsingException) {
            processor.exceptionHandlerRegistry.handleException(
                e,
                RequestEventContext(badProtocol(), jsonAdapter),
                processor.tracer
            ).json()
        }
    }

    private fun parseEvent(payload: String?): RawEvent {
        return try {
            jsonAdapter.fromJson(payload!!)
        } catch (e: Throwable) {
            throw EventParsingException(e)
        }
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

    private fun ResponseEvent.json() = jsonAdapter.toJson(this)
}
