package br.com.guiabolso.events.server.exception

object ExceptionHandlerRegistryFactory {

    @JvmStatic
    fun exceptionHandler() = ExceptionHandlerRegistry()

    @JvmStatic
    fun bypassExceptionHandler(wrapExceptionAndEvent: Boolean = true): ExceptionHandlerRegistry {
        val handler = ExceptionHandlerRegistry()

        handler.register(Exception::class.java) { ex, request, _ ->
            if (wrapExceptionAndEvent) {
                throw BypassedException(ex, request)
            } else {
                throw ex
            }
        }

        return handler
    }

}

