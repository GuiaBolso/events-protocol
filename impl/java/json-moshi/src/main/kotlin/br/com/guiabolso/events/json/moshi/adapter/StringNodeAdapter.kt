package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.moshi.nullSafeAdapterFor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class StringNodeAdapter(private val moshi: Moshi) : JsonAdapter<StringNode>() {
    private val stringAdapter = moshi.nullSafeAdapterFor<String>()

    override fun fromJson(reader: JsonReader): StringNode? {
        return stringAdapter.fromJson(reader)?.run { StringNode(this) }
    }

    override fun toJson(writer: JsonWriter, value: StringNode?) {
        stringAdapter.toJson(writer, value?.value)
    }
}
