package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.TreeNode
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object TreeNodeAdapter : TypeAdapter<TreeNode>() {

    override fun write(writer: JsonWriter, value: TreeNode?) {
        if (value != null) {
            writer.serialize(value)
        } else {
            writer.nullValue()
        }
    }

    private fun JsonWriter.serialize(value: TreeNode) {
        beginObject()
        value.forEach { key, jsonNode -> name(key).write(jsonNode) }
        endObject()
    }

    override fun read(reader: JsonReader): TreeNode {
        return reader.deserialize()
    }

    private fun JsonReader.deserialize(): TreeNode {
        return TreeNode().apply {
            beginObject()
            while (hasNext()) {
                val key = nextName()
                val jsonNode = readJsonNode()
                ensureNotExists(key, jsonNode, path)
                this[key] = jsonNode
            }
            endObject()
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
}
