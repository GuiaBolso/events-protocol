package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.TreeNode

data class RawEvent(
    val name: String?,
    val version: Int?,
    val id: String?,
    val flowId: String?,
    val payload: JsonNode?,
    val identity: TreeNode?,
    val auth: TreeNode?,
    val metadata: TreeNode?
)
