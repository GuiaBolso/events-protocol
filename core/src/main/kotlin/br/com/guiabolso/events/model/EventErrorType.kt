package br.com.guiabolso.events.model

sealed class EventErrorType(open val typeName: String) {

    companion object {
        @JvmStatic
        fun getErrorType(errorType: String) = when (errorType) {
            "error" -> Generic
            "notFound" -> NotFound
            else -> Unknown(errorType)
        }
    }

    object Generic : EventErrorType("error")
    object NotFound : EventErrorType("notFound")
    data class Unknown(override val typeName: String) : EventErrorType(typeName)

}