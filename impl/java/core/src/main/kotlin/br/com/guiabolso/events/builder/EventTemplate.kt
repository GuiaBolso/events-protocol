package br.com.guiabolso.events.builder

import br.com.guiabolso.events.exception.MissingEventInformationException
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.treeNodeOrNull
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.utils.EventUtils

class EventTemplate(private val jsonAdapter: JsonAdapter) {
    var name: String? = null
    var version: Int? = null
    var id = EventUtils.eventId
    var flowId = EventUtils.flowId
    var payload: Any? = null
    var identity: Any? = null
    var auth: Any? = null
    var metadata: Any? = null

    fun toRequestEvent() = RequestEvent(
        name = this.name ?: throw MissingEventInformationException("Missing event name."),
        version = this.version ?: throw MissingEventInformationException("Missing event version."),
        id = this.id ?: throw MissingEventInformationException("Missing event id."),
        flowId = this.flowId ?: throw MissingEventInformationException("Missing event flowId."),
        payload = convertPayload(),
        identity = convertToJsonObjectOrEmpty(this.identity),
        auth = convertToJsonObjectOrEmpty(this.auth),
        metadata = convertToJsonObjectOrEmpty(this.metadata)
    )

    fun toResponseEvent() = ResponseEvent(
        name = this.name ?: throw MissingEventInformationException("Missing event name."),
        version = this.version ?: throw MissingEventInformationException("Missing event version."),
        id = this.id ?: throw MissingEventInformationException("Missing event id."),
        flowId = this.flowId ?: throw MissingEventInformationException("Missing event flowId."),
        payload = convertPayload(),
        identity = convertToJsonObjectOrEmpty(this.identity),
        auth = convertToJsonObjectOrEmpty(this.auth),
        metadata = convertToJsonObjectOrEmpty(this.metadata)
    )

    private fun convertPayload() = when (this.payload) {
        null -> JsonNull
        else -> jsonAdapter.toJsonTree(this.payload)
    }

    private fun convertToJsonObjectOrEmpty(value: Any?) = when (value) {
        null -> TreeNode()
        JsonNull -> TreeNode()
        else -> jsonAdapter.toJsonTree(value).treeNodeOrNull ?: TreeNode()
    }
}
