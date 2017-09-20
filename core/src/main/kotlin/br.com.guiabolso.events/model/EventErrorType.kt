package br.com.guiabolso.events.model

sealed class EventErrorType(val typeName: String) {

    companion object {
        @JvmStatic
        fun getErrorType(errorType: String) = when (errorType) {
            "error" -> Generic()
            "notFound" -> NotFound()
            else -> Unknown(errorType)
        }
    }

    class Generic : EventErrorType("error")
    class NotFound : EventErrorType("notFound")
    class Unknown(typeName: String) : EventErrorType(typeName)

}