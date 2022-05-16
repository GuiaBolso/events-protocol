package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.TreeNode
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object TreeNodeAdapter : TypeAdapter<TreeNode>() {

    override fun write(writer: JsonWriter, value: TreeNode?) {
        if (value != null) writer.serialize(value)
        else writer.nullValue()
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
        val treeNode = TreeNode()
        beginObject()
        while (hasNext()) {
            val key = nextName()
            treeNode[key] = readJsonNode()
        }
        endObject()

        return treeNode
    }
}
