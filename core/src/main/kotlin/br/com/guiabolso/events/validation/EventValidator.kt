package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

object EventValidator {

    fun validateAsResponseEvent(rawEvent: RawEvent) = ResponseEvent(
            name = rawEvent.name.required("name"),
            version = rawEvent.version.required("version"),
            id = rawEvent.id.required("id"),
            flowId = rawEvent.flowId.required("flowId"),
            payload = rawEvent.payload.required("payload"),
            identity = rawEvent.identity.required("identity"),
            auth = rawEvent.auth.required("auth"),
            metadata = rawEvent.metadata.required("metadata")
    )

    fun validateAsRequestEvent(rawEvent: RawEvent) = RequestEvent(
            name = rawEvent.name.required("name"),
            version = rawEvent.version.required("version"),
            id = rawEvent.id.required("id"),
            flowId = rawEvent.flowId.required("flowId"),
            payload = rawEvent.payload.required("payload"),
            identity = rawEvent.identity.required("identity"),
            auth = rawEvent.auth.required("auth"),
            metadata = rawEvent.metadata.required("metadata")
    )

    private fun <T> T?.required(name: String): T {
        return this ?: throw IllegalArgumentException(name)
    }

}