package br.com.guiabolso.events.exception

import br.com.guiabolso.events.model.EventErrorType.BadProtocol

class MissingRequiredProtocolPropertyException(property: String) : EventException(
    code = "MISSING_PROTOCOL_REQUIRED_PROPERTY",
    parameters = mapOf("property" to property),
    type = BadProtocol,
    expected = false,
    cause = null
)