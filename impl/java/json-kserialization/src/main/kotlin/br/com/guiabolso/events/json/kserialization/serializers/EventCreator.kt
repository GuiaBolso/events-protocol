package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

sealed interface EventCreator<T : Event> {
    fun create(
        name: String,
        version: Int,
        id: String,
        flowId: String,
        payload: JsonNode,
        identity: TreeNode,
        auth: TreeNode,
        metadata: TreeNode
    ): T
}

object RequestEventCreator : EventCreator<RequestEvent> {
    override fun create(
        name: String,
        version: Int,
        id: String,
        flowId: String,
        payload: JsonNode,
        identity: TreeNode,
        auth: TreeNode,
        metadata: TreeNode
    ): RequestEvent {
        return RequestEvent(
            name = name,
            version = version,
            id = id,
            flowId = flowId,
            payload = payload,
            metadata = metadata,
            auth = auth,
            identity = identity
        )
    }
}

object ResponseEventCreator : EventCreator<ResponseEvent> {

    override fun create(
        name: String,
        version: Int,
        id: String,
        flowId: String,
        payload: JsonNode,
        identity: TreeNode,
        auth: TreeNode,
        metadata: TreeNode
    ): ResponseEvent {
        return ResponseEvent(
            name = name,
            version = version,
            id = id,
            flowId = flowId,
            payload = payload,
            metadata = metadata,
            auth = auth,
            identity = identity
        )
    }
}
