package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.Event
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Suppress("MagicNumber")
class EventSerializer<T : Event>(serialName: String, private val eventCreator: EventCreator<T>) : KSerializer<T> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(serialName) {
        element<String>(elementName = Event::name.name)
        element<Int>(elementName = Event::version.name)
        element<String>(elementName = Event::id.name)
        element<String>(elementName = Event::flowId.name)
        element(elementName = Event::payload.name, JsonNodeSerializer.descriptor)
        element(elementName = Event::identity.name, TreeNodeSerializer.descriptor)
        element(elementName = Event::auth.name, TreeNodeSerializer.descriptor)
        element(elementName = Event::metadata.name, TreeNodeSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeIntElement(descriptor, 1, value.version)
            encodeStringElement(descriptor, 2, value.id)
            encodeStringElement(descriptor, 3, value.flowId)
            encodeSerializableElement(descriptor, 4, JsonNodeSerializer, value.payload)
            encodeSerializableElement(descriptor, 5, TreeNodeSerializer, value.identity)
            encodeSerializableElement(descriptor, 6, TreeNodeSerializer, value.auth)
            encodeSerializableElement(descriptor, 7, TreeNodeSerializer, value.metadata)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): T {
        return decoder.decodeStructure(descriptor) {
            var version: Int = -1
            lateinit var id: String
            lateinit var name: String
            lateinit var flowId: String
            lateinit var auth: TreeNode
            lateinit var payload: JsonNode
            lateinit var identity: TreeNode
            lateinit var metadata: TreeNode

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, index)
                    1 -> version = decodeIntElement(descriptor, index)
                    2 -> id = decodeStringElement(descriptor, index)
                    3 -> flowId = decodeStringElement(descriptor, index)
                    4 -> payload = decodeSerializableElement(descriptor, index, JsonNodeSerializer)
                    5 -> identity = decodeSerializableElement(descriptor, index, TreeNodeSerializer)
                    6 -> auth = decodeSerializableElement(descriptor, index, TreeNodeSerializer)
                    7 -> metadata = decodeSerializableElement(descriptor, index, TreeNodeSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            eventCreator.create(
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
