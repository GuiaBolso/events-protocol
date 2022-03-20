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
            error("Bad protocol message, trying to serialize a null Event")
        }

        writer.beginObject()
        writer.name("name").value(value.name)
        writer.name("version").value(value.version)
        writer.name("id").value(value.id)
        writer.name("flowId").value(value.flowId)
        writer.name("payload").run { jsonNodeAdapter.toJson(this, value.payload) }
        writer.name("identity").run { jsonNodeAdapter.toJson(this, value.identity) }
        writer.name("auth").run { jsonNodeAdapter.toJson(this, value.auth) }
        writer.name("metadata").run { jsonNodeAdapter.toJson(this, value.metadata) }
        writer.endObject()
    }
}
