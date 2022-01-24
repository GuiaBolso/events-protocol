package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token
import com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY
import com.squareup.moshi.JsonReader.Token.BEGIN_OBJECT
import com.squareup.moshi.JsonReader.Token.BOOLEAN
import com.squareup.moshi.JsonReader.Token.NULL
import com.squareup.moshi.JsonReader.Token.NUMBER
import com.squareup.moshi.JsonReader.Token.STRING
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class JsonNodeAdapter(val moshi: Moshi) : JsonAdapter<JsonNode>() {

    override fun fromJson(reader: JsonReader): JsonNode {
        return adapterFor(reader.peek())?.fromJson(reader) ?: JsonNull
    }

    private fun adapterFor(token: Token) = when (token) {
        BEGIN_OBJECT -> adapterOf<TreeNode>()
        BEGIN_ARRAY -> adapterOf<ArrayNode>()
        NUMBER, STRING, BOOLEAN, NULL -> adapterOf<PrimitiveNode>()
        else -> null
    }

    private inline fun <reified T> adapterOf() = moshi.adapter(T::class.java).nullSafe()

    override fun toJson(writer: JsonWriter, value: JsonNode?) {
        if (value == null) writer.nullValue()
        else moshi.adapter<JsonNode>(value::class.java).toJson(writer, value)
    }
}
