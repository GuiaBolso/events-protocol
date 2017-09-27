package br.com.guiabolso.events.handler

class SimpleEventHandlerDiscovery : EventHandlerDiscovery {

    private val handlers = mutableMapOf<Pair<String, Int>, EventHandler>()

    fun add(eventName: String, eventVersion: Int, handler: EventHandler) {
        handlers[eventName to eventVersion] = handler
    }

    override fun eventHandlerFor(eventName: String, eventVersion: Int) = handlers[eventName to eventVersion]
}