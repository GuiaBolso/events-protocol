package br.com.guiabolso.events.server.exception

object ExceptionHandlerRegistryFactory {

    @JvmStatic
    fun exceptionHandler() = ExceptionHandlerRegistry()

    @JvmStatic
    fun bypassExceptionHandler(): ExceptionHandlerRegistry {
        val handler = ExceptionHandlerRegistry()

        handler.register(Exception::class.java) { ex, request, _ ->
            throw BypassedException(ex, request)
        }

        return handler
    }

}

