package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import com.google.gson.stream.JsonWriter

fun JsonWriter.write(jsonNode: JsonNode) {
    when (jsonNode) {
        is PrimitiveNode -> PrimitiveNodeAdapter.write(this, jsonNode)
        is TreeNode -> TreeNodeAdapter.write(this, jsonNode)
        is ArrayNode -> ArrayNodeAdapter.write(this, jsonNode)
    }
}
