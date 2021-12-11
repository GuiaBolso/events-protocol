@file:Suppress("FunctionName")

package br.com.guiabolso.events.json

import br.com.guiabolso.events.json.JsonNode.PrimitiveNode
import br.com.guiabolso.events.json.JsonNode.TreeNode

fun JsonPrimitive(value: Number) = PrimitiveNode.NumberNode(value)

fun JsonPrimitive(value: String) = PrimitiveNode.StringNode(value)

fun JsonPrimitive(value: Boolean) = PrimitiveNode.BooleanNode(value)

fun JsonNode.asPrimitiveStringNode(): PrimitiveNode.StringNode {
    return this as PrimitiveNode.StringNode
}

fun JsonNode.asString() = this.asPrimitiveStringNode().value

fun JsonNode.asPrimitiveBooleanNode(): PrimitiveNode.BooleanNode {
    return this as PrimitiveNode.BooleanNode
}

fun JsonNode.asPrimitiveNumberNode() = this as PrimitiveNode.NumberNode

fun JsonNode.asTreeNode() = this as TreeNode
