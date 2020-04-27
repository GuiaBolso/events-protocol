package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface EventHandlerRegistry : EventHandlerDiscovery {

    fun addAll(vararg handler: EventHandler) = addAll(handler.toList())
    
    fun addAll(handlers: Collection<EventHandler>) = handlers.forEach(::add)
    
    fun add(handler: EventHandler)

    fun add(eventName: String, eventVersion: Int, handler: (RequestEvent) -> ResponseEvent)

}
