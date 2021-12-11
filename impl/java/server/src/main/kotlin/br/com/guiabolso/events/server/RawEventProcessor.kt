package br.com.guiabolso.events.server

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventCoroutineContextForwarder.withCoroutineContext
import br.com.guiabolso.events.context.EventThreadContextManager.withContext
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventNotFoundException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import java.util.UUID

open class RawEventProcessor
@JvmOverloads
constructor(
    val discovery: EventHandlerDiscovery,
    val exceptionHandlerRegistry: ExceptionHandlerRegistry,
    val tracer: Tracer = DefaultTracer,
    val eventValidator: EventValidator = StrictEventValidator(),
    val traceOperationPrefix: String = ""
) {

    suspend fun processEvent(rawEvent: RawEvent): ResponseEvent {
        return try {
            val event = eventValidator.validateAsRequestEvent(rawEvent)
            val handler = eventHandlerFor(event.name, event.version)

            withContext(EventContext(event.id, event.flowId)).use {
                withCoroutineContext {
                    startProcessingEvent(event)
                    handler.handle(event)
                }
            }
        } catch (e: Exception) {
            exceptionHandlerRegistry.handleException(e, rawEvent.asRequestEvent(), tracer)
        } finally {
            eventProcessFinished()
        }
    }

    private fun eventHandlerFor(eventName: String, eventVersion: Int): EventHandler {
        return discovery.eventHandlerFor(eventName, eventVersion) ?: throw EventNotFoundException()
    }

    protected fun startProcessingEvent(event: RequestEvent) {
        tracer.setOperationName("$traceOperationPrefix${event.name}:V${event.version}")
        tracer.addProperty("EventID", event.id)
        tracer.addProperty("FlowID", event.flowId)
        tracer.addProperty("UserID", event.userIdAsString ?: "unknown")
        tracer.addProperty("Origin", event.origin ?: "unknown")
    }

    protected fun eventProcessFinished() {
        tracer.clear()
    }

    private fun RawEvent.asRequestEvent() = RequestEvent(
        name = name ?: BadProtocol.typeName,
        version = version ?: 1,
        id = id ?: UUID.randomUUID().toString(),
        flowId = flowId ?: UUID.randomUUID().toString(),
        payload = payload ?: TreeNode(),
        identity = identity ?: TreeNode(),
        auth = auth ?: TreeNode(),
        metadata = metadata ?: TreeNode()
    )
}
