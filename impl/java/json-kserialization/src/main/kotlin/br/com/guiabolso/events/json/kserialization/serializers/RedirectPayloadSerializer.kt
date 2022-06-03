package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.RedirectPayload
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object RedirectPayloadSerializer : KSerializer<RedirectPayload> {
    private val paramSerializer = MapSerializer(String.serializer(), JsonNodeSerializer)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(RedirectPayload::class.qualifiedName!!) {
        element(RedirectPayload::url.name, serialDescriptor<String>())
        element(RedirectPayload::queryParameters.name, paramSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): RedirectPayload {
        lateinit var url: String
        lateinit var params: Map<String, JsonNode>

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> url = decodeStringElement(descriptor, index)
                    1 -> params = decodeSerializableElement(descriptor, 1, paramSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        return RedirectPayload(url = url, queryParameters = params)
    }

    override fun serialize(encoder: Encoder, value: RedirectPayload) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.url)
            encodeSerializableElement(descriptor, 1, paramSerializer, value.queryParameters)
        }
    }
}
