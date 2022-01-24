@file:Suppress("FunctionName")

package br.com.guiabolso.events.json

val JsonNode.arrayNode get() = castOrError<ArrayNode>()

val JsonNode.treeNode get() = castOrError<TreeNode>()

val JsonNode.treeNodeOrNull get() = this as? TreeNode

val JsonNode.primitiveNode get() = castOrError<PrimitiveNode>()

val JsonNode.primitiveNodeOrNull get() = this as? PrimitiveNode

val JsonNode.jsonNull get() = castOrError<JsonNull>()

private inline fun <reified T> JsonNode.castOrError(): T {
    return this as? T ?: throw IllegalArgumentException("JsonNode is not a ${T::class.java}")
}

val PrimitiveNode.int get() = value.toInt()

val PrimitiveNode.intOrNull get() = value.toIntOrNull()

val PrimitiveNode.long get() = value.toLong()

val PrimitiveNode.longOrNull get() = value.toLongOrNull()

val PrimitiveNode.float get() = value.toFloat()

val PrimitiveNode.floatOrNull get() = value.toFloatOrNull()

val PrimitiveNode.double get() = value.toDouble()

val PrimitiveNode.doubleOrNull get() = value.toDoubleOrNull()

val PrimitiveNode.boolean get() = value.toBooleanStrict()

val PrimitiveNode.booleanOrNull get() = value.toBooleanStrictOrNull()

val PrimitiveNode.stringOrNull get() = if (this is JsonNull) null else value

fun PrimitiveNode(value: String?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Number?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Boolean?) = if (value == null) JsonNull else JsonLiteral(value)
