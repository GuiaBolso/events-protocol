package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.Event
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class EventTypeAdapter<T : Event?>(
    private val readerDelegate: TypeAdapter<T>,
    private val jsonNodeAdapter: TypeAdapter<JsonNode>
) : TypeAdapter<T>() {

    override fun read(reader: JsonReader): T {
        return readerDelegate.read(reader)
    }

    override fun write(writer: JsonWriter, value: T?) {
        if (value == null) {
            error("Bad protocol message, trying to serialize a null Event")
        }

        writer.beginObject()
        writer.name("name").value(value.name)
        writer.name("version").value(value.version)
        writer.name("id").value(value.id)
        writer.name("flowId").value(value.flowId)
        writer.name("payload").run { jsonNodeAdapter.write(this, value.payload) }
        writer.name("identity").run { jsonNodeAdapter.write(this, value.identity) }
        writer.name("auth").run { jsonNodeAdapter.write(this, value.auth) }
        writer.name("metadata").run { jsonNodeAdapter.write(this, value.metadata) }
        writer.endObject()
    }
}
