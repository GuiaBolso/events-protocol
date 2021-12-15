package br.com.guiabolso.events.validation

import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

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

    private fun JsonNode?.requiredJsonObject(name: String): JsonNode.TreeNode {
        if (this == null || this !is JsonNode.TreeNode) throw EventValidationException(name)
        return this
    }
}
