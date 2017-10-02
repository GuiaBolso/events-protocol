package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import org.slf4j.LoggerFactory

class SimpleEventHandlerRegistry : EventHandlerDiscovery {

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleEventHandlerRegistry::class.java)
    }

    private val handlers = mutableMapOf<Pair<String, Int>, EventHandler>()

    fun add(eventName: String, eventVersion: Int, handler: EventHandler) {
        logger.info("Registering event handler for $eventName V$eventVersion")
        handlers[eventName to eventVersion] = handler
    }

    fun add(eventName: String, eventVersion: Int, handler: (RequestEvent) -> ResponseEvent) {
        add(eventName, eventVersion, LambdaEventHandler(handler))
    }

    override fun eventHandlerFor(eventName: String, eventVersion: Int) = handlers[eventName to eventVersion]

}