package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventCoroutineContextForwarder.withCoroutineContext
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface SuspendingEventHandler : EventHandler {

    suspend fun coHandle(event: RequestEvent): ResponseEvent

    override fun handle(event: RequestEvent): ResponseEvent =
        withCoroutineContext(EventContext(event.id, event.flowId)) {
            coHandle(event)
        }
}
