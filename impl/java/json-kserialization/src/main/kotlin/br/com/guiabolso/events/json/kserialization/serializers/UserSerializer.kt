package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.model.User
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
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

@OptIn(ExperimentalSerializationApi::class)
object UserSerializer : KSerializer<User> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(User::class.qualifiedName!!) {
        element("id", serialDescriptor<String>())
        element("type", serialDescriptor<String>())
    }

    private val stringKSerializer = String.serializer().nullable

    private val longKSerializer = Long.serializer().nullable

    override fun deserialize(decoder: Decoder): User {
        var id: Long? = null
        var type: String? = null
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                id = decodeNullableSerializableElement(descriptor, 0, longKSerializer)
                type = decodeNullableSerializableElement(descriptor, 1, stringKSerializer)
            } else while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeNullableSerializableElement(descriptor, index, longKSerializer)
                    1 -> type = decodeNullableSerializableElement(descriptor, index, stringKSerializer)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        return User(id, type)
    }

    override fun serialize(encoder: Encoder, value: User) {
        encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(descriptor, 0, longKSerializer, value.id)
            encodeNullableSerializableElement(descriptor, 1, stringKSerializer, value.type)
        }
    }
}
