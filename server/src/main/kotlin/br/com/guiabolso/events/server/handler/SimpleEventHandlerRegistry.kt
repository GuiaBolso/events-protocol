package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

class SimpleEventHandlerRegistry : EventHandlerDiscovery {

    private val handlers = mutableMapOf<Pair<String, Int>, EventHandler>()

    fun add(eventName: String, eventVersion: Int, handler: EventHandler) {
        handlers[eventName to eventVersion] = handler
    }

    fun add(eventName: String, eventVersion: Int, handler: (RequestEvent) -> ResponseEvent) {
        handlers[eventName to eventVersion] = LambdaEventHandler(handler)
    }

    override fun eventHandlerFor(eventName: String, eventVersion: Int) = handlers[eventName to eventVersion]

}