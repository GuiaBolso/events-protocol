package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class JsonNodeAdapter(moshi: Moshi) : JsonAdapter<JsonNode>() {

   private val nodeAdapters = mapOf(
        JsonNode.TreeNode::class to TreeNodeAdapter(moshi).nullSafe(),
        JsonNode.ArrayNode::class to TreeNodeAdapter(moshi).nullSafe(),
        JsonNode.PrimitiveNode.NumberNode::class to TreeNodeAdapter(moshi).nullSafe(),
        JsonNode.PrimitiveNode.StringNode::class to StringNodeAdapter(moshi).nullSafe(),
        JsonNode.PrimitiveNode.BooleanNode::class to TreeNodeAdapter(moshi).nullSafe()
    )

    override fun fromJson(reader: JsonReader): JsonNode {
        val jsonNode: JsonNode? = when (reader.peek()) {
            JsonReader.Token.BEGIN_OBJECT ->  nodeAdapters[JsonNode.TreeNode::class]!!.fromJson(reader)
            JsonReader.Token.STRING -> nodeAdapters[JsonNode.PrimitiveNode.StringNode::class]!!.fromJson(reader)
            else -> null
        }

        return jsonNode ?: JsonNode.JsonNull
    }

    override fun toJson(writer: JsonWriter, value: JsonNode?) {
        TODO("Not yet implemented")
    }

}
