package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

internal object PrimitiveNodeSerializer : JsonSerializer<PrimitiveNode>() {
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
