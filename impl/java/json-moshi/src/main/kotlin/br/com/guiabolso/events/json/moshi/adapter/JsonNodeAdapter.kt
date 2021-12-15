package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.ArrayNode
import br.com.guiabolso.events.json.JsonNode.JsonNull
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.BooleanNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class JsonNodeAdapter(val moshi: Moshi) : JsonAdapter<JsonNode>() {

    override fun fromJson(reader: JsonReader): JsonNode {
        return adapterFor(reader.peek())?.fromJson(reader) ?: JsonNull
    }

    private fun adapterFor(token: Token) = when (token) {
        Token.NUMBER -> adapterOf<NumberNode>()
        Token.STRING -> adapterOf<StringNode>()
        Token.BOOLEAN -> adapterOf<BooleanNode>()
        Token.BEGIN_OBJECT -> adapterOf<TreeNode>()
        Token.BEGIN_ARRAY -> adapterOf<ArrayNode>()
        else -> null
    }

    private inline fun <reified T> adapterOf() = moshi.adapter(T::class.java).nullSafe()

    override fun toJson(writer: JsonWriter, value: JsonNode?) {
        if (value == null) writer.nullValue()
        else moshi.adapter<JsonNode>(value::class.java).toJson(writer, value)
    }
}
