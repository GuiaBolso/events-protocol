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

    @Deprecated(
        message = "This configuration doest throws all exceptions to keep the same behavior of versions older then 5.x.x",
        replaceWith = ReplaceWith(
            expression = "bypassAllExceptionHandler",
            imports = [
                "br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.bypassAllExceptionHandler"
            ]
        )
    )
    @JvmStatic
    fun bypassExceptionHandler(wrapExceptionAndEvent: Boolean = true) = ExceptionHandlerRegistry().apply {
        register(EventValidationException::class.java, BadProtocolExceptionHandler)
        register(EventNotFoundException::class.java, EventNotFoundExceptionHandler)
        register(Exception::class.java, BypassExceptionHandler(wrapExceptionAndEvent))
    }

    @JvmStatic
    fun bypassAllExceptionHandler(wrapExceptionAndEvent: Boolean = true) = ExceptionHandlerRegistry().apply {
        register(Exception::class.java, BypassExceptionHandler(wrapExceptionAndEvent))
    }
}
