package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.exception.EventException
import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.server.exception.EventNotFoundException

object ExceptionHandlerRegistryFactory {

    @JvmStatic
    fun emptyExceptionHandler() = ExceptionHandlerRegistry()

    @JvmStatic
    fun exceptionHandler() = ExceptionHandlerRegistry().apply {
        register(EventValidationException::class.java, BadProtocolExceptionHandler)
        register(EventNotFoundException::class.java, EventNotFoundExceptionHandler)
        register(EventException::class.java, EventExceptionExceptionHandler)
    }

    @JvmStatic
    fun bypassAllExceptionHandler(wrapExceptionAndEvent: Boolean = true) = ExceptionHandlerRegistry().apply {
        register(Exception::class.java, BypassExceptionHandler(wrapExceptionAndEvent))
    }
}
