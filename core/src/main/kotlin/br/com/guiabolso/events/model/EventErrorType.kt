package br.com.guiabolso.events.model

sealed class EventErrorType(open val typeName: String) {

    companion object {
        @JvmStatic
        fun getErrorType(errorType: String) = when (errorType) {
            "error" -> Generic
            "notFound" -> NotFound
            "badRequest" -> BadRequest
            "unauthorized" -> Unauthorized
            "forbidden" -> Forbidden
            else -> Unknown(errorType)
        }
    }

    object Generic : EventErrorType("error")
    object BadRequest : EventErrorType("badRequest")
    object Unauthorized : EventErrorType("unauthorized")
    object NotFound : EventErrorType("notFound")
    object Forbidden : EventErrorType("forbidden")
    data class Unknown(override val typeName: String) : EventErrorType(typeName)

}