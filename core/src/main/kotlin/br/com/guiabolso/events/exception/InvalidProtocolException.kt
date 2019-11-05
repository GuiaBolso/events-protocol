package br.com.guiabolso.events.exception

import br.com.guiabolso.events.model.EventErrorType.BadProtocol

class InvalidProtocolException(cause: Throwable) : EventException(
    code = "INVALID_COMMUNICATION_PROTOCOL",
    parameters = mapOf("message" to cause.message),
    type = BadProtocol,
    expected = false,
    cause = cause
)