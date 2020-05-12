package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

class LambdaEventHandler(
    override val eventName: String,
    override val eventVersion: Int,
    private val func: (RequestEvent) -> ResponseEvent
) : EventHandler {

    override fun handle(event: RequestEvent) = func(event)
}
