package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.ArrayNode
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

object ArrayNodeAdapter : TypeAdapter<ArrayNode>() {

    override fun write(writer: JsonWriter, value: ArrayNode?) {
        if (value != null) writer.serialize(value)
        else writer.nullValue()
    }

    private fun JsonWriter.serialize(value: ArrayNode) {
        beginArray()
        value.forEach { jsonNode -> write(jsonNode) }
        endArray()
    }

    override fun read(reader: JsonReader): ArrayNode {
        return reader.deserialize()
    }

    private fun JsonReader.deserialize(): ArrayNode {
        return ArrayNode().apply {
            beginArray()
            while (hasNext()) add(element = readJsonNode())
            endArray()
        }
    }
}
