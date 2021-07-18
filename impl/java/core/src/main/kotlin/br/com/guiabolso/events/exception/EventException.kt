package br.com.guiabolso.events.exception

import br.com.guiabolso.events.model.EventErrorType

open class EventException(
    val code: String,
    val parameters: Map<String, Any?>,
    val type: EventErrorType = EventErrorType.Generic,
    cause: Throwable? = null,
    val expected: Boolean = false
) : RuntimeException(code, cause)