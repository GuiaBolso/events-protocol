package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class TreeNodeAdapter(moshi: Moshi) : JsonAdapter<JsonNode.TreeNode>() {
    private val keyAdapter = moshi.adapter(String::class.java).nonNull()
    private val valueAdapter = moshi.adapter(JsonNode::class.java).nullSafe()

    override fun fromJson(reader: JsonReader): JsonNode.TreeNode {
        if (reader.peek() == JsonReader.Token.NULL) {
            throw JsonDataException("Unexpected null value, wrap this adapter with nullSafe adapter")
        }

        return JsonNode.TreeNode().apply {
            reader.beginObject()
            while (reader.hasNext()) {
                reader.promoteNameToValue()
                val name: String = keyAdapter.fromJson(reader)!!
                val value = valueAdapter.fromJson(reader) ?: JsonNode.JsonNull
                if (contains(name)) {
                    val current = this[name]
                    throw JsonDataException(
                        "JsonNode key '$name' has multiple values at path ${reader.path}:$current and $value"
                    )
                }
                this[name] = value
            }
            reader.endObject()
        }
    }

    override fun toJson(writer: JsonWriter, value: JsonNode.TreeNode?) {
        TODO("Not yet implemented")
    }
}
