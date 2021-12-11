package br.com.guiabolso.events.json

import br.com.guiabolso.events.json.JsonNode.TreeNode

fun <T> TreeNode.withCheckedJsonNull(checkedParam: String, block: (node: TreeNode) -> T?): T? {
    val jsonNode = this[checkedParam]
    return if (jsonNode is JsonNode.JsonNull || jsonNode == null) null
    else block(this)
}

fun TreeNode.getValue(key: String): JsonNode {
    return this[key] ?: throw NoSuchElementException("Key $key is missing in the TreeNode.")
}

fun TreeNode.getAsPrimitiveStringNode(key: String) = this.getValue(key).asPrimitiveStringNode()

fun TreeNode.getAsPrimitiveNumberNode(key: String) = this.getValue(key).asPrimitiveNumberNode()

fun TreeNode.getAsPrimitiveBoolenaNode(key: String) = this.getValue(key).asPrimitiveBooleanNode()
