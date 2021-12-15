package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.Event
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class EventProtocolAdapter<T : Event?>(
    private val delegate: JsonAdapter<T>,
    private val jsonNodeAdapter: JsonAdapter<JsonNode>
) : JsonAdapter<T>() {

    override fun fromJson(reader: JsonReader): T? {
        return delegate.fromJson(reader)
    }

    override fun toJson(writer: JsonWriter, value: T?) {
        if (value == null) {
            throw IllegalStateException("Bad protocol message, trying to serialize a null Event")
        }

        val serializeNulls = writer.serializeNulls
        writer.serializeNulls = true

        writer.beginObject()
        writer.name("name").value(value.name)
        writer.name("version").value(value.version)
        writer.name("id").value(value.id)
        writer.name("flowId").value(value.flowId)

        writer.name("payload")
        jsonNodeAdapter.toJson(writer, value.payload)

        writer.name("identity")
        jsonNodeAdapter.toJson(writer, value.identity)

        writer.name("auth")
        jsonNodeAdapter.toJson(writer, value.auth)

        writer.name("metadata")
        jsonNodeAdapter.toJson(writer, value.metadata)
        writer.endObject()

        writer.serializeNulls = serializeNulls
    }
}
