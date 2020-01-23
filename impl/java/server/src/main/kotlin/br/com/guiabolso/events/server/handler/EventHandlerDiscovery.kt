package br.com.guiabolso.events.server.handler

interface EventHandlerDiscovery {

    fun eventHandlerFor(eventName: String, eventVersion: Int): EventHandler?

}