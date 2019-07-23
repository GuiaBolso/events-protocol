package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import org.slf4j.LoggerFactory

@Deprecated(
    message = "Used only to validate events of applications that don't fully implement this protocol.",
    replaceWith = ReplaceWith("StrictEventValidator", "br.com.guiabolso.events.validation.StrictEventValidator")
)
class LenientEventValidator : EventValidator {

    override fun validateAsResponseEvent(rawEvent: RawEvent?): ResponseEvent {
        val name = rawEvent?.name.required("name")
        val version = rawEvent?.version.required("version")
        val flowId = rawEvent?.flowId.required("flowId")

        val missingProperties = mutableListOf<String>()

        val responseEvent = ResponseEvent(
            name = name,
            version = version,
            id = rawEvent?.id.required("id"),
            flowId = flowId,
            payload = rawEvent?.payload.getPayload(missingProperties, "payload"),
            identity = rawEvent?.identity.getAsJsonObject(missingProperties, "identity"),
            auth = rawEvent?.auth.getAsJsonObject(missingProperties, "auth"),
            metadata = rawEvent?.metadata.getAsJsonObject(missingProperties, "metadata")
        )

        if (missingProperties.isNotEmpty()) {
            logger.warn("Event $name:V$version($flowId) with missing required properties [${missingProperties.joinToString()}].")
        }

        return responseEvent
    }

    override fun validateAsRequestEvent(rawEvent: RawEvent?): RequestEvent {
        val name = rawEvent?.name.required("name")
        val version = rawEvent?.version.required("version")
        val flowId = rawEvent?.flowId.required("flowId")

        val missingProperties = mutableListOf<String>()

        val request = RequestEvent(
            name = name,
            version = version,
            id = rawEvent?.id.required("id"),
            flowId = flowId,
            payload = rawEvent?.payload.getPayload(missingProperties, "payload"),
            identity = rawEvent?.identity.getAsJsonObject(missingProperties, "identity"),
            auth = rawEvent?.auth.getAsJsonObject(missingProperties, "auth"),
            metadata = rawEvent?.metadata.getAsJsonObject(missingProperties, "metadata")
        )

        if (missingProperties.isNotEmpty()) {
            logger.warn("Event $name:V$version($flowId) missing required properties [${missingProperties.joinToString()}].")
        }

        return request
    }

    private fun JsonElement?.getPayload(missingProperties: MutableList<String>, name: String): JsonElement {
        return if (this == null) {
            missingProperties.add(name)
            JsonNull.INSTANCE
        } else {
            this
        }
    }

    private fun JsonElement?.getAsJsonObject(missingProperties: MutableList<String>, name: String): JsonObject =
        when (this) {
            is JsonObject -> this
            else -> {
                missingProperties.add(name)
                JsonObject()
            }
        }

    companion object {
        @Suppress("DEPRECATION")
        private val logger = LoggerFactory.getLogger(LenientEventValidator::class.java)
    }

}