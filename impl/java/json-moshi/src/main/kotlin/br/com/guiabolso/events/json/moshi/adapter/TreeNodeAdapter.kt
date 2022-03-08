package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.nullSafeAdapterFor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class TreeNodeAdapter(moshi: Moshi) : JsonAdapter<TreeNode>() {
    private val jsonNodeAdapter = moshi.nullSafeAdapterFor<JsonNode>()

    override fun fromJson(reader: JsonReader): TreeNode {
        return TreeNode().apply {
            reader.beginObject()
            while (reader.hasNext()) {
                reader.promoteNameToValue()
                val name: String = reader.nextString()
                val value = jsonNodeAdapter.fromJson(reader) ?: JsonNull
                ensureNotExists(name, value, reader.path)
                this[name] = value
            }
            reader.endObject()
        }
    }

    private fun TreeNode.ensureNotExists(name: String, current: JsonNode, path: String) {
        if (contains(name)) {
            val existent = this[name]
            throw JsonDataException(
                "JsonNode key '$name' has multiple values at path $path, values $existent and $current"
            )
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
