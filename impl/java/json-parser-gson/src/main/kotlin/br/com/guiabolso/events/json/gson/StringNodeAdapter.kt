package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class StringNodeAdapter(private val moshi: Moshi) : JsonAdapter<JsonNode.PrimitiveNode.StringNode>() {
    private val stringAdapter = moshi.adapter(String::class.java).nullSafe()

    override fun fromJson(reader: JsonReader): JsonNode.PrimitiveNode.StringNode? {
        return stringAdapter.fromJson(reader)?.run { JsonNode.PrimitiveNode.StringNode(this) }
    }

    override fun toJson(writer: JsonWriter, value: JsonNode.PrimitiveNode.StringNode?) {
        TODO("Not yet implemented")
    }
}
