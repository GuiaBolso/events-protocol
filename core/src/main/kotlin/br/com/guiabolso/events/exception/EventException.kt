package br.com.guiabolso.events.exception

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventErrorType.Generic
import br.com.guiabolso.events.model.EventMessage

open class EventException(
    val code: String,
    val parameters: Map<String, Any?>,
    val type: EventErrorType = Generic,
    val expected: Boolean = false,
    cause: Throwable? = null
) : RuntimeException(code, cause) {

    val eventMessage = EventMessage(code, parameters)

}