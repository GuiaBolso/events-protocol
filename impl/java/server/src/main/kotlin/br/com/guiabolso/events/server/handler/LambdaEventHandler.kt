package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.ResponseEvent

class LambdaEventHandler(
    override val eventName: String,
    override val eventVersion: Int,
    private val func: suspend (RequestEventContext) -> ResponseEvent
) : EventHandler {

    override suspend fun handle(event: RequestEventContext): ResponseEvent = func(event)
}
