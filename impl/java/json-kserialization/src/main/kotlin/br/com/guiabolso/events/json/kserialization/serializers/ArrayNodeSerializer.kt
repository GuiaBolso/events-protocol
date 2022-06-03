package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.ArrayNode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ArrayNodeSerializer : KSerializer<ArrayNode> {

    private val serializer = ListSerializer(JsonNodeSerializer)

    object ArrayNodeDescriptor : SerialDescriptor by serializer.descriptor {
        @ExperimentalSerializationApi
        override val serialName: String = ArrayNode::class.qualifiedName!!
    }

    override val descriptor: SerialDescriptor = ArrayNodeDescriptor

    override fun serialize(encoder: Encoder, value: ArrayNode) {
        serializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ArrayNode {
        return ArrayNode(
            serializer.deserialize(decoder).toMutableList()
        )
    }
}
