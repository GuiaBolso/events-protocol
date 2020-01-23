package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface EventHandlerRegistry : EventHandlerDiscovery {

    fun add(handler: EventHandler)

    fun add(eventName: String, eventVersion: Int, handler: (RequestEvent) -> ResponseEvent)

}