package br.com.guiabolso.events.model

sealed class EventErrorType(open val typeName: String) {

    companion object {
        @JvmStatic
        fun getErrorType(errorType: String) = when (errorType) {
            "error" -> Generic
            "notFound" -> NotFound
            "eventNotFound" -> EventNotFound
            "badRequest" -> BadRequest
            "badProtocol" -> BadProtocol
            "unauthorized" -> Unauthorized
            "forbidden" -> Forbidden
            "userDenied" -> UserDenied
            "resourceDenied" -> ResourceDenied
            "expired" -> Expired
            else -> Unknown(errorType)
        }
    }

    object Generic : EventErrorType("error")
    object BadRequest : EventErrorType("badRequest")
    object BadProtocol : EventErrorType("badProtocol")
    object Unauthorized : EventErrorType("unauthorized")
    object NotFound : EventErrorType("notFound")
    object EventNotFound : EventErrorType("eventNotFound")
    object Forbidden : EventErrorType("forbidden")
    object UserDenied : EventErrorType("userDenied")
    object ResourceDenied : EventErrorType("resourceDenied")
    object Expired : EventErrorType("expired")
    data class Unknown(override val typeName: String) : EventErrorType(typeName)

}