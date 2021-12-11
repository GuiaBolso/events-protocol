package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type

object JsonNodeTypeAdapter : JsonDeserializer<JsonNode> {
    override fun deserialize(json: JsonElement?, type: Type, context: JsonDeserializationContext): JsonNode {
        if (json == null || json is JsonNull) return JsonNode.JsonNull

        return when (json) {
            is JsonObject -> createTreeNodeFor(json, type, context)
            is JsonPrimitive -> createPrimitiveNodeFor(json, type, context)
            is JsonArray -> createArrayNodeFor(json, type, context)
            else -> error("This should never be reached")
        }
    }

    private fun createArrayNodeFor(json: JsonArray, type: Type, context: JsonDeserializationContext): JsonNode {
        return json.fold(JsonNode.ArrayNode()) { acc, jsonElement ->
            acc.apply { add(element = context.deserialize(jsonElement, type)) }
        }
    }

    private fun createPrimitiveNodeFor(json: JsonPrimitive, type: Type, context: JsonDeserializationContext): JsonNode {
        return when {
            json.isBoolean -> JsonNode.PrimitiveNode.BooleanNode(json.asBoolean)
            json.isString -> JsonNode.PrimitiveNode.StringNode(json.asString)
            json.isNumber -> JsonNode.PrimitiveNode.NumberNode(json.asNumber)
            else -> error("This should never be reached!")
        }
    }

    private fun createTreeNodeFor(
        json: JsonObject,
        type: Type,
        context: JsonDeserializationContext
    ): JsonNode {
        return json
            .entrySet()
            .fold(JsonNode.TreeNode()) { tree, (key, value) ->
                tree[key] = context.deserialize(value, type)
                tree
            }
    }
}
