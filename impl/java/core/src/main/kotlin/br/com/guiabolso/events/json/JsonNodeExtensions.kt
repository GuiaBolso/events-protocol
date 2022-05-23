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

val JsonNode.string get() = castOrError<PrimitiveNode>().value
val JsonNode.stringOrNull get() = castOrError<PrimitiveNode>().run { if (this is JsonNull) null else value }
val JsonNode.boolean get() = castOrError<PrimitiveNode>().value.toBooleanStrict()
val JsonNode.booleanOrNull get() = castOrError<PrimitiveNode>().value.toBooleanStrictOrNull()
val JsonNode.int get() = castOrError<PrimitiveNode>().value.toInt()
val JsonNode.intOrNull get() = castOrError<PrimitiveNode>().value.toIntOrNull()
val JsonNode.long get() = castOrError<PrimitiveNode>().value.toLong()
val JsonNode.longOrNull get() = castOrError<PrimitiveNode>().value.toLongOrNull()
val JsonNode.double get() = castOrError<PrimitiveNode>().value.toDouble()
val JsonNode.doubleOrNull get() = castOrError<PrimitiveNode>().value.toDoubleOrNull()
