package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.BooleanNode
import br.com.guiabolso.events.json.moshi.nullSafeAdapterFor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class BooleanNodeAdapter(moshi: Moshi) : JsonAdapter<BooleanNode>() {
    private val adapter = moshi.nullSafeAdapterFor<Boolean>()

    override fun fromJson(reader: JsonReader): BooleanNode? {
        return adapter.fromJson(reader)?.run { BooleanNode(this) }
    }

    override fun toJson(writer: JsonWriter, value: BooleanNode?) {
        adapter.toJson(writer, value?.value)
    }
}
