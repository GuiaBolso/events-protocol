package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.moshi.nullSafeAdapterFor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class TreeNodeAdapter(private val moshi: Moshi) : JsonAdapter<TreeNode>() {
    private val jsonNodeAdapter = moshi.nullSafeAdapterFor<JsonNode>()

    override fun fromJson(reader: JsonReader): TreeNode {
        return TreeNode().apply {
            reader.beginObject()
            while (reader.hasNext()) {
                reader.promoteNameToValue()
                val name: String = reader.nextString()
                val value = jsonNodeAdapter.fromJson(reader) ?: JsonNode.JsonNull
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

    override fun toJson(writer: JsonWriter, value: TreeNode?) {
        if (value == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        for (entry in value) {
            writer.name(entry.key)
            jsonNodeAdapter.toJson(writer, entry.value)
        }
        writer.endObject()
    }
}
