package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class NumberNodeAdapter : JsonAdapter<NumberNode>() {

    override fun fromJson(reader: JsonReader): NumberNode? {
        if (reader.peek() != JsonReader.Token.NUMBER) return null

        val numberString = reader.nextString()
        return runCatching { numberString.toInt() }
            .recoverCatching { numberString.toLong() }
            .recoverCatching { numberString.toDouble() }
            .map { NumberNode(it) }
            .getOrNull()
    }

    override fun toJson(writer: JsonWriter, value: NumberNode?) {
        writer.value(value?.value)
    }
}
