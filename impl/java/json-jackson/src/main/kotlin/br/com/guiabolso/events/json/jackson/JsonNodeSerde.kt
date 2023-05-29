package br.com.guiabolso.events.json.jackson

/**
 * Contains the appropriate serializers and deserializers for [br.com.guiabolso.events.json.JsonNode]
 */

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.math.BigDecimal

internal object JsonNodeDeser: JsonDeserializer<JsonNode>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonNode {
        return when {
            p.currentToken == JsonToken.VALUE_NULL -> JsonNull
            p.currentToken.isBoolean -> PrimitiveNode(p.readValueAs(Boolean::class.java))
            p.currentToken.isNumeric -> PrimitiveNode(p.readValueAs(BigDecimal::class.java))
            p.currentToken.isScalarValue -> PrimitiveNode(p.readValueAs(String::class.java))
            else -> throw IllegalStateException("token type [${p.currentToken}] is not mapped to any recognized type")
        }
    }
}

internal object PrimitiveNodeSer: JsonSerializer<PrimitiveNode>() {
    override fun serialize(value: PrimitiveNode, gen: JsonGenerator, serializers: SerializerProvider) {
        when (value) {
            is JsonNull -> gen.writeNull()
            is JsonLiteral -> when {
                value.isBoolean -> gen.writeBoolean(value.value.toBooleanStrict())
                value.isNumber -> gen.writeNumber(value.value.toBigDecimal())
                value.isString -> gen.writeString(value.value)
            }
        }
    }
}