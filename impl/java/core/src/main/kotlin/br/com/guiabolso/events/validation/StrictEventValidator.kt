package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class StrictEventValidator : EventValidator {

    override fun validateAsResponseEvent(rawEvent: RawEvent?) = ResponseEvent(
        name = rawEvent?.name.required("name"),
        version = rawEvent?.version.required("version"),
        id = rawEvent?.id.required("id"),
        flowId = rawEvent?.flowId.required("flowId"),
        payload = rawEvent?.payload.required("payload"),
        identity = rawEvent?.identity.requiredJsonObject("identity"),
        auth = rawEvent?.auth.requiredJsonObject("auth"),
        metadata = rawEvent?.metadata.requiredJsonObject("metadata")
    )

    override fun validateAsRequestEvent(rawEvent: RawEvent?) = RequestEvent(
        name = rawEvent?.name.required("name"),
        version = rawEvent?.version.required("version"),
        id = rawEvent?.id.required("id"),
        flowId = rawEvent?.flowId.required("flowId"),
        payload = rawEvent?.payload.required("payload"),
        identity = rawEvent?.identity.requiredJsonObject("identity"),
        auth = rawEvent?.auth.requiredJsonObject("auth"),
        metadata = rawEvent?.metadata.requiredJsonObject("metadata")
    )

    private fun JsonElement?.requiredJsonObject(name: String): JsonObject {
        if (this == null || !this.isJsonObject) throw EventValidationException(name)
        return this.asJsonObject
    }
}
