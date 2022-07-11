@file:Suppress("FunctionName")

package br.com.guiabolso.events.json

fun PrimitiveNode(value: String?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Number?) = if (value == null) JsonNull else JsonLiteral(value)
fun PrimitiveNode(value: Boolean?) = if (value == null) JsonNull else JsonLiteral(value)

fun String?.toPrimitiveNode() = PrimitiveNode(this)
fun Boolean?.toPrimitiveNode() = PrimitiveNode(this)
fun Number?.toPrimitiveNode() = PrimitiveNode(this)
