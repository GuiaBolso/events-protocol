package br.com.guiabolso.events.json

val JsonNode.primitiveNode get() = castOrError<PrimitiveNode>()

val JsonNode.primitiveNodeOrNull get() = this as? PrimitiveNode

val JsonNode.jsonNull get() = castOrError<JsonNull>()

val JsonNode.arrayNode get() = castOrError<ArrayNode>()

val JsonNode.treeNode get() = castOrError<TreeNode>()

val JsonNode.treeNodeOrNull get() = this as? TreeNode

private inline fun <reified T> JsonNode.castOrError(): T {
    return this as? T ?: throw IllegalArgumentException("JsonNode is not a ${T::class.java}")
}
