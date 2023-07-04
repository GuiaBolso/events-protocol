package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.ResponseEvent
import org.slf4j.LoggerFactory

class SimpleEventHandlerRegistry : EventHandlerRegistry {

    private val handlers = mutableMapOf<Pair<String, Int>, EventHandler>()

    override fun add(handler: EventHandler) {
        logger.info("Registering event handler for ${handler.eventName} V${handler.eventVersion}")
        if (handlers.containsKey(handler.eventName to handler.eventVersion)) {
            throw IllegalStateException(
                "Duplicated event. Event ${handler.eventName} V${handler.eventVersion} is already registered"
            )
        }
        handlers[handler.eventName to handler.eventVersion] = handler
    }

    override fun add(
        eventName: String,
        eventVersion: Int,
        handler: suspend (RequestEventContext) -> ResponseEvent
    ) {
        add(LambdaEventHandler(eventName, eventVersion, handler))
    }

    override fun eventHandlerFor(eventName: String, eventVersion: Int) = handlers[eventName to eventVersion]

    companion object {
        private val logger = LoggerFactory.getLogger(SimpleEventHandlerRegistry::class.java)
    }
}
