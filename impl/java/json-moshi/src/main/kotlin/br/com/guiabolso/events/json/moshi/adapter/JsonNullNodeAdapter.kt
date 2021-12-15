package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode.JsonNull
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class JsonNullNodeAdapter : JsonAdapter<JsonNull>() {
    override fun fromJson(reader: JsonReader): JsonNull {
        reader.nextNull<Any?>()
        return JsonNull
    }

    override fun toJson(writer: JsonWriter, value: JsonNull?) {
        writer.nullValue()
    }
}
