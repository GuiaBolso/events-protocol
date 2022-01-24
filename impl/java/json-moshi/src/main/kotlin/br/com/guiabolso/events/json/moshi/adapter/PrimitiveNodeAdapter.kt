package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token
import com.squareup.moshi.JsonWriter
import java.math.BigDecimal

class PrimitiveNodeAdapter : JsonAdapter<PrimitiveNode>() {

    override fun fromJson(reader: JsonReader): PrimitiveNode {
        return when (reader.peek()) {
            Token.STRING -> JsonLiteral(reader.nextString())
            Token.BOOLEAN -> JsonLiteral(reader.nextBoolean())
            Token.NUMBER -> JsonLiteral(BigDecimal(reader.nextString()))
            Token.NULL -> {
                reader.nextNull<Any?>()
                JsonNull
            }
            else -> error("Invalid token for primitive type")
        }
    }

    override fun toJson(writer: JsonWriter, value: PrimitiveNode?) {
        when {
            value == null || value is JsonNull -> writer.nullValue()
            value.isString -> writer.value(value.value)
            value.isNumber -> writer.value(BigDecimal(value.value))
            value.isBoolean -> writer.value(value.value.toBooleanStrict())
        }
    }
}
