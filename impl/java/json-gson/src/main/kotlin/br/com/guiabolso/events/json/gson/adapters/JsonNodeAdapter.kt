package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNode
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object JsonNodeAdapter : TypeAdapter<JsonNode>() {

    override fun write(writer: JsonWriter, value: JsonNode?) {
        if (value != null) {
            writer.write(value)
        } else {
            writer.nullValue()
        }
    }

    override fun read(reader: JsonReader): JsonNode {
        return reader.readJsonNode()
    }
}
