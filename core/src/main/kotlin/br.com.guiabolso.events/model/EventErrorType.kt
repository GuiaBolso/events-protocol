package br.com.guiabolso.events.model

sealed class EventErrorType(val typeName: String) {

    class GenericError : EventErrorType("error")
    class NotFound : EventErrorType("notFound")
    class Unknown(typeName: String) : EventErrorType(typeName)

}