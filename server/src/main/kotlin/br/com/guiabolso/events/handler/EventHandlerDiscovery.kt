package br.com.guiabolso.events.handler

interface EventHandlerDiscovery {

    fun eventHandlerFor(eventName: String, eventVersion: Int): EventHandler?

}