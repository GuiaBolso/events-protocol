@file:Suppress("FunctionName")

package br.com.guiabolso.events.json

val PrimitiveNode.int get() = value.toInt()

val PrimitiveNode.intOrNull get() = value.toIntOrNull()

val PrimitiveNode.long get() = value.toLong()

val PrimitiveNode.longOrNull get() = value.toLongOrNull()

val PrimitiveNode.double get() = value.toDouble()

val PrimitiveNode.doubleOrNull get() = value.toDoubleOrNull()

val PrimitiveNode.boolean get() = value.toBooleanStrict()

val PrimitiveNode.booleanOrNull get() = value.toBooleanStrictOrNull()

val PrimitiveNode.stringOrNull get() = if (this is JsonNull) null else value

fun PrimitiveNode(value: String?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Number?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Boolean?) = if (value == null) JsonNull else JsonLiteral(value)

fun String?.toPrimitiveNode() = PrimitiveNode(this)
fun Boolean?.toPrimitiveNode() = PrimitiveNode(this)
fun Number?.toPrimitiveNode() = PrimitiveNode(this)
