package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RawEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@OptIn(ExperimentalSerializationApi::class)
@Suppress("MagicNumber")
object RawEventSerializer : KSerializer<RawEvent> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(RawEvent::class.qualifiedName!!) {
        element<String>(elementName = Event::name.name)
        element<Int>(elementName = Event::version.name)
        element<String>(elementName = Event::id.name)
        element<String>(elementName = Event::flowId.name)
        element(elementName = Event::payload.name, JsonNodeSerializer.descriptor)
        element(elementName = Event::identity.name, TreeNodeSerializer.descriptor)
        element(elementName = Event::auth.name, TreeNodeSerializer.descriptor)
        element(elementName = Event::metadata.name, TreeNodeSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: RawEvent) {
        encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(descriptor, 0, String.serializer().nullable, value.name)
            encodeNullableSerializableElement(descriptor, 1, Int.serializer().nullable, value.version)
            encodeNullableSerializableElement(descriptor, 2, String.serializer().nullable, value.id)
            encodeNullableSerializableElement(descriptor, 3, String.serializer().nullable, value.flowId)
            encodeNullableSerializableElement(descriptor, 4, JsonNodeSerializer.nullable, value.payload)
            encodeNullableSerializableElement(descriptor, 5, TreeNodeSerializer.nullable, value.identity)
            encodeNullableSerializableElement(descriptor, 6, TreeNodeSerializer.nullable, value.auth)
            encodeNullableSerializableElement(descriptor, 7, TreeNodeSerializer.nullable, value.metadata)
        }
    }

    override fun deserialize(decoder: Decoder): RawEvent {
        return decoder.decodeStructure(descriptor) {
            var version: Int? = null
            var id: String? = null
            var name: String? = null
            var flowId: String? = null
            var auth: TreeNode? = null
            var payload: JsonNode? = null
            var identity: TreeNode? = null
            var metadata: TreeNode? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                    1 -> version = decodeNullableSerializableElement(descriptor, index, Int.serializer().nullable)
                    2 -> id = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                    3 -> flowId = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                    4 -> payload = decodeNullableSerializableElement(descriptor, index, JsonNodeSerializer.nullable)
                    5 -> identity = decodeNullableSerializableElement(descriptor, index, TreeNodeSerializer.nullable)
                    6 -> auth = decodeNullableSerializableElement(descriptor, index, TreeNodeSerializer.nullable)
                    7 -> metadata = decodeNullableSerializableElement(descriptor, index, TreeNodeSerializer.nullable)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            RawEvent(
                name = name,
                version = version,
                id = id,
                flowId = flowId,
                payload = payload,
                metadata = metadata,
                auth = auth,
                identity = identity
            )
        }
    }
}
