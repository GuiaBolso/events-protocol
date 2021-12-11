package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

object JsonNodeFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<out JsonNode>? {
        val rawType = Types.getRawType(type)
        if (!rawType.isAssignableFrom(JsonNode::class.java)) return null



        return JsonNodeAdapter(moshi)
    }
}
