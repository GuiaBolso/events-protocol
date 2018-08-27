package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import org.slf4j.LoggerFactory

class SimpleEventHandlerRegistry : EventHandlerRegistry {

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleEventHandlerRegistry::class.java)
    }

    private val handlers = mutableMapOf<Pair<String, Int>, EventHandler>()

    override fun add(handler: EventHandler) {
        logger.info("Registering event handler for ${handler.eventName} V${handler.eventVersion}")
        handlers[handler.eventName to handler.eventVersion] = handler
    }

    override fun add(eventName: String, eventVersion: Int, handler: (RequestEvent) -> ResponseEvent) {
        add(LambdaEventHandler(eventName, eventVersion, handler))
    }

    override fun eventHandlerFor(eventName: String, eventVersion: Int) = handlers[eventName to eventVersion]

}