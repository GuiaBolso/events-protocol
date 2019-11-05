package br.com.guiabolso.events.exception

import br.com.guiabolso.events.model.EventErrorType.BadRequest

class MissingRequiredParameterException(parameter: String) : EventException(
    "MISSING_REQUIRED_PARAMETER",
    mapOf("name" to parameter),
    BadRequest
)