package br.com.guiabolso.events.json.moshi.factory

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.ArrayNode
import br.com.guiabolso.events.json.JsonNode.JsonNull
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.BooleanNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.moshi.adapter.ArrayNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.BooleanNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.JsonNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.JsonNullNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.NumberNodeAdapter
import br.com.guiabolso.events.json.moshi.adapter.StringNodeAdapter
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
            StringNode::class.java.isAssignableFrom(rawType) -> StringNodeAdapter(moshi).serializeNulls()
            NumberNode::class.java.isAssignableFrom(rawType) -> NumberNodeAdapter(moshi).serializeNulls()
            BooleanNode::class.java.isAssignableFrom(rawType) -> BooleanNodeAdapter(moshi).serializeNulls()
            ArrayNode::class.java.isAssignableFrom(rawType) -> ArrayNodeAdapter(moshi).serializeNulls()
            TreeNode::class.java.isAssignableFrom(rawType) -> TreeNodeAdapter(moshi).serializeNulls()
            JsonNull::class.java.isAssignableFrom(rawType) -> JsonNullNodeAdapter().serializeNulls()
            else -> JsonNodeAdapter(moshi).serializeNulls()
        }
    }
}
