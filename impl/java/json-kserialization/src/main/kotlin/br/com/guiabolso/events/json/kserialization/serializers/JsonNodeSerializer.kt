package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.kserialization.asJsonDecoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
object JsonNodeSerializer : KSerializer<JsonNode> {

    override val descriptor: SerialDescriptor =
        buildSerialDescriptor(JsonNode::class.qualifiedName!!, PolymorphicKind.SEALED) {
            element("PrimitiveNode", defer { PrimitiveNodeSerializer.descriptor })
            element("JsonNull", defer { JsonNullSerializer.descriptor })
            element("JsonLiteral", defer { JsonLiteralSerializer.descriptor })
            element("TreeNode", defer { TreeNodeSerializer.descriptor })
            element("ArrayNode", defer { ArrayNodeSerializer.descriptor })
        }

    override fun serialize(encoder: Encoder, value: JsonNode) {
        when (value) {
            is PrimitiveNode -> encoder.encodeSerializableValue(PrimitiveNodeSerializer, value)
            is TreeNode -> encoder.encodeSerializableValue(TreeNodeSerializer, value)
            is ArrayNode -> encoder.encodeSerializableValue(ArrayNodeSerializer, value)
        }
    }

    override fun deserialize(decoder: Decoder): JsonNode {
        return decoder.asJsonDecoder().decodeJsonElement().toJsonNode()
    }
}

@ExperimentalSerializationApi
private fun defer(deferred: () -> SerialDescriptor): SerialDescriptor = object : SerialDescriptor {

    private val original: SerialDescriptor by lazy(deferred)

    override val serialName: String get() = original.serialName
    override val kind: SerialKind get() = original.kind
    override val elementsCount: Int get() = original.elementsCount
    override fun getElementName(index: Int): String = original.getElementName(index)
    override fun getElementIndex(name: String): Int = original.getElementIndex(name)
    override fun getElementAnnotations(index: Int): List<Annotation> = original.getElementAnnotations(index)
    override fun getElementDescriptor(index: Int): SerialDescriptor = original.getElementDescriptor(index)
    override fun isElementOptional(index: Int): Boolean = original.isElementOptional(index)
}

private fun JsonElement.toJsonNode(): JsonNode {
    return when (this) {
        is JsonObject -> toTreeNode()
        is JsonArray -> toArrayNode()
        is JsonPrimitive -> toPrimitiveNode()
    }
}

private fun JsonArray.toArrayNode() = ArrayNode(mapTo(mutableListOf()) { it.toJsonNode() })

private fun JsonObject.toTreeNode(): TreeNode {
    return TreeNode(nodes = map { (k, v) -> k to v.toJsonNode() }.associateTo(mutableMapOf()) { it })
}

private fun JsonPrimitive.toPrimitiveNode(): PrimitiveNode {
    return if (isString) JsonLiteral(content)
    else if (this is JsonNull) br.com.guiabolso.events.json.JsonNull
    else parsePrimitiveNode(content)
}

private fun parsePrimitiveNode(content: String): JsonLiteral {
    val boolean = content.toBooleanStrictOrNull()
    if (boolean != null) return JsonLiteral(boolean)

    val number = content.toLongOrNull() ?: content.toDoubleOrNull() ?: content.toBigDecimalOrNull()
    return if (number != null) JsonLiteral(number)
    else error("bad primitive value $content")
}
