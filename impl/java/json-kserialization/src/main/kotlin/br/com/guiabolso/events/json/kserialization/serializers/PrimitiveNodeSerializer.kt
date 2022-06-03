package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(InternalSerializationApi::class)
internal object PrimitiveNodeSerializer : KSerializer<PrimitiveNode> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor(PrimitiveNode::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PrimitiveNode) {
        if (value is JsonNull) return encoder.encodeSerializableValue(JsonNullSerializer, JsonNull)

        return encoder.encodeSerializableValue(JsonLiteralSerializer, value as JsonLiteral)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): PrimitiveNode {
        return if (decoder.decodeNotNullMark()) decoder.decodeSerializableValue(JsonLiteralSerializer)
        else decoder.decodeSerializableValue(JsonNullSerializer)
    }
}
