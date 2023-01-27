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

val JsonNode.string get() = stringOrNull!!
val JsonNode.stringOrNull get() = primitiveNodeOrNull?.run { if (this is JsonNull) null else value }

val JsonNode.boolean get() = primitiveNode.value.toBooleanStrict()
val JsonNode.booleanOrNull get() = primitiveNodeOrNull?.value?.toBooleanStrictOrNull()

val JsonNode.int get() = primitiveNode.value.toInt()
val JsonNode.intOrNull get() = primitiveNodeOrNull?.value?.toIntOrNull()

val JsonNode.long get() = primitiveNode.value.toLong()
val JsonNode.longOrNull get() = primitiveNodeOrNull?.value?.toLongOrNull()

val JsonNode.double get() = primitiveNode.value.toDouble()
val JsonNode.doubleOrNull get() = primitiveNodeOrNull?.value?.toDoubleOrNull()

@Suppress("UNCHECKED_CAST")
fun <T : JsonNode> T.deepCopy(): T {
    val copy =
        when (this) {
            is PrimitiveNode -> this
            is TreeNode -> this.deepCopy()
            is ArrayNode -> this.deepCopy()
            else -> error("Unknown JsonNode type ${this::class.qualifiedName}")
        }

    return copy as T
}

private fun TreeNode.deepCopy(): TreeNode {
    return TreeNode(
        nodes = map { (k, v) -> k to v.deepCopy() }.toMap(mutableMapOf())
    )
}

private fun ArrayNode.deepCopy(): ArrayNode {
    return ArrayNode(
        elements = mapTo(mutableListOf()) { it.deepCopy() }
    )
}
