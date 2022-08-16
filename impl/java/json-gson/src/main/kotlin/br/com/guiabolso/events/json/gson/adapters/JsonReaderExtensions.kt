package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNode
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken

fun JsonReader.readJsonNode(): JsonNode {
    return when (peek()) {
        JsonToken.STRING, JsonToken.BOOLEAN, JsonToken.NULL, JsonToken.NUMBER -> PrimitiveNodeAdapter.read(this)
        JsonToken.BEGIN_OBJECT -> TreeNodeAdapter.read(this)
        JsonToken.BEGIN_ARRAY -> ArrayNodeAdapter.read(this)
        else -> error("No adapter for token ${peek()}")
    }
}
