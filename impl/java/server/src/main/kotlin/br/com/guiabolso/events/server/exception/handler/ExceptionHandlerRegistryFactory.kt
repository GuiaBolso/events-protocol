package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.server.exception.EventNotFoundException

object ExceptionHandlerRegistryFactory {

    @JvmStatic
    fun emptyExceptionHandler() = ExceptionHandlerRegistry()

    @JvmStatic
    fun exceptionHandler() = ExceptionHandlerRegistry().apply {
        register(EventValidationException::class.java, BadProtocolExceptionHandler)
        register(EventNotFoundException::class.java, EventNotFoundExceptionHandler)
    }

    @JvmStatic
    fun bypassExceptionHandler(wrapExceptionAndEvent: Boolean = true) = ExceptionHandlerRegistry().apply {
        register(EventValidationException::class.java, BadProtocolExceptionHandler)
        register(EventNotFoundException::class.java, EventNotFoundExceptionHandler)
        register(Exception::class.java, BypassExceptionHandler(wrapExceptionAndEvent))
    }

}
