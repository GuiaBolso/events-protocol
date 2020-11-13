package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

class LambdaEventHandler(
    override val eventName: String,
    override val eventVersion: Int,
    private val func: suspend (RequestEvent) -> ResponseEvent
) : EventHandler {

    override suspend fun handle(event: RequestEvent) = func(event)
}
