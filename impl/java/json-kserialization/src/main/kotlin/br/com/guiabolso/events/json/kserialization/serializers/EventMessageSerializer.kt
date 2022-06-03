package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.EventMessage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

object EventMessageSerializer : KSerializer<EventMessage> {
    private val paramsSerializer = MapSerializer(String.serializer(), JsonNodeSerializer.nullable)

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(EventMessage::class.qualifiedName!!) {
        element(EventMessage::code.name, serialDescriptor<String>())
        element(EventMessage::parameters.name, paramsSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): EventMessage {
        lateinit var code: String
        lateinit var params: Map<String, JsonNode?>

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> code = decodeStringElement(descriptor, index)
                    1 -> params = decodeSerializableElement(descriptor, index, paramsSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        return EventMessage(code = code, parameters = params)
    }

    override fun serialize(encoder: Encoder, value: EventMessage) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.code)
            encodeSerializableElement(descriptor, 1, paramsSerializer, value.parameters)
        }
    }
}
