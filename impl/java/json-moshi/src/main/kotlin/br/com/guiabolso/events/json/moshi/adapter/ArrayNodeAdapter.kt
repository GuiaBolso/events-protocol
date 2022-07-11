package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.moshi.nullSafeAdapterFor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class ArrayNodeAdapter(moshi: Moshi) : JsonAdapter<ArrayNode>() {
    private val jsonNodeAdapter = moshi.nullSafeAdapterFor<JsonNode>().serializeNulls()

    override fun fromJson(reader: JsonReader): ArrayNode {
        return ArrayNode().apply {
            reader.beginArray()
            while (reader.hasNext()) {
                add(jsonNodeAdapter.fromJson(reader) ?: JsonNull)
            }
            reader.endArray()
        }
    }

    override fun toJson(writer: JsonWriter, value: ArrayNode?) {
        if (value != null) {
            writer.beginArray()
            value.forEach { element -> jsonNodeAdapter.toJson(writer, element) }
            writer.endArray()
        } else writer.nullValue()
    }
}
