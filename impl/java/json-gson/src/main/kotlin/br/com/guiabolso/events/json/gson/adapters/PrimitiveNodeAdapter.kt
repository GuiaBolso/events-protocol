package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.math.BigDecimal

object PrimitiveNodeAdapter : TypeAdapter<PrimitiveNode>() {

    override fun write(writer: JsonWriter, value: PrimitiveNode?) {
        when {
            value == null || value is JsonNull -> writer.nullValue()
            value.isString -> writer.value(value.value)
            value.isNumber -> writer.value(BigDecimal(value.value))
            value.isBoolean -> writer.value(value.value.toBooleanStrict())
        }
    }

    override fun read(reader: JsonReader): PrimitiveNode {
        return when (reader.peek()) {
            JsonToken.STRING -> PrimitiveNode(reader.nextString())
            JsonToken.BOOLEAN -> PrimitiveNode(reader.nextBoolean())
            JsonToken.NUMBER -> PrimitiveNode(BigDecimal(reader.nextString()))
            JsonToken.NULL -> {
                reader.nextNull()
                JsonNull
            }
            else -> error("Invalid token for primitive type")
        }
    }
}
