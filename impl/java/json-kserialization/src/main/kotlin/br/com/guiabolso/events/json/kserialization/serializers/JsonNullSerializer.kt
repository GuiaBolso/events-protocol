package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
object JsonNullSerializer : KSerializer<JsonNull> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor(JsonNull::class.qualifiedName!!, SerialKind.ENUM)

    override fun deserialize(decoder: Decoder): JsonNull {
        decoder.decodeNull()
        return JsonNull
    }

    override fun serialize(encoder: Encoder, value: JsonNull) {
        encoder.encodeNull()
    }
}
