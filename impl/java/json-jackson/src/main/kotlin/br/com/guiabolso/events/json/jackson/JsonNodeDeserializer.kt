package br.com.guiabolso.events.json.jackson

/**
 * Contains the appropriate serializers and deserializers for [br.com.guiabolso.events.json.JsonNode]
 */

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.math.BigDecimal

typealias jsonNodeListType = MutableList<JsonNode>
private val jsonNodeListTypeRef = object : TypeReference<jsonNodeListType>() {}
typealias jsonNodeTreeType = MutableMap<String, JsonNode>
private val jsonNodeTreeTypeRef = object : TypeReference<jsonNodeTreeType>() {}

internal object JsonNodeDeserializer : JsonDeserializer<JsonNode>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): JsonNode {
        return parse(p)
            ?: throw IllegalStateException("token type [${p.currentToken}] is not mapped to any recognized type")
    }

    private fun parse(p: JsonParser): JsonNode? {

        val node = parsePrimitive(p)
            ?: parseArray(p)
            ?: parseObject(p)

        return node
    }

    private fun parsePrimitive(p: JsonParser): PrimitiveNode? {
        return when {
            p.currentToken == JsonToken.VALUE_NULL -> JsonNull
            p.currentToken.isBoolean -> PrimitiveNode(p.readValueAs(Boolean::class.java))
            p.currentToken.isNumeric -> PrimitiveNode(p.readValueAs(BigDecimal::class.java))
            p.currentToken.isScalarValue -> PrimitiveNode(p.readValueAs(String::class.java))
            else -> null
        }
    }

    private fun parseArray(p: JsonParser): ArrayNode? {
        return if (p.currentToken == JsonToken.START_ARRAY) {
            ArrayNode(p.readValueAs<jsonNodeListType>(jsonNodeListTypeRef))
        } else {
            null
        }
    }

    private fun parseObject(p: JsonParser): TreeNode? {
        return if (p.currentToken == JsonToken.START_OBJECT) {
            TreeNode(p.readValueAs<jsonNodeTreeType>(jsonNodeTreeTypeRef))
        } else {
            null
        }
    }
}
