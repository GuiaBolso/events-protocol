package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.kserialization.asJsonDecoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

object JsonLiteralSerializer : KSerializer<JsonLiteral> {
    override val descriptor = PrimitiveSerialDescriptor(
        kind = PrimitiveKind.STRING,
        serialName = JsonLiteral::class.qualifiedName!!
    )

    override fun deserialize(decoder: Decoder): JsonLiteral {
        val element = decoder.asJsonDecoder().decodeJsonElement()
        require(element is JsonPrimitive)
        require(element !is JsonNull)

        return jsonLiteral(element)
    }

    private fun jsonLiteral(
        element: JsonPrimitive,
    ): JsonLiteral {
        val content = element.content
        return if (element.isString) JsonLiteral(content)
        else parseJsonLiteral(content)
    }

    private fun parseJsonLiteral(content: String): JsonLiteral {
        val boolean = content.toBooleanStrictOrNull()
        if (boolean != null) return JsonLiteral(boolean)

        val number = content.toLongOrNull() ?: content.toDoubleOrNull() ?: content.toBigDecimalOrNull()
        return if (number != null) JsonLiteral(number)
        else error("bad primitive value $content")
    }

    override fun serialize(encoder: Encoder, value: JsonLiteral) {
        with(value) {
            when {
                isString -> encoder.encodeString(this.value)
                isBoolean -> encoder.encodeBoolean(this.value.toBooleanStrict())
                isNumber -> encodeNumber(encoder)
                else -> error("unreachable")
            }
        }
    }

    private fun JsonLiteral.encodeNumber(encoder: Encoder) {
        value.toLongOrNull()?.run {
            encoder.encodeLong(this)
            return
        }

        value.toDoubleOrNull()?.run { return encoder.encodeDouble(this) }

        value.toBigDecimalOrNull()?.run { encoder.encodeString(this.toPlainString()) }
    }
}
