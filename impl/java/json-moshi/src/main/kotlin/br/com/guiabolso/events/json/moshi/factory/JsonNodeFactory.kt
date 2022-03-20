package br.com.guiabolso.events.json.moshi.factory

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.adapter.ArrayNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.JsonNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.PrimitiveNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.TreeNodeAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

object JsonNodeFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<out JsonNode>? {
        val rawType = Types.getRawType(type)
        if (!JsonNode::class.java.isAssignableFrom(rawType)) return null

        return when {
            PrimitiveNode::class.java.isAssignableFrom(rawType) -> PrimitiveNodeAdapter()
            ArrayNode::class.java.isAssignableFrom(rawType) -> ArrayNodeAdapter(moshi)
            TreeNode::class.java.isAssignableFrom(rawType) -> TreeNodeAdapter(moshi)
            else -> JsonNodeAdapter(moshi)
        }
    }
}
