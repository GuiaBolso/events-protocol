package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class JsonNodeSerializerTest : StringSpec({
    val primitiveNode = PrimitiveNode(true)
    val arrayNode = ArrayNode(PrimitiveNode(1.0))
    val treeNode = TreeNode("key" to PrimitiveNode(10), "other" to PrimitiveNode("value"))

    "should write json node as json successfully" {
        Json.encodeToString(JsonNodeSerializer, JsonNull) shouldBe "null"
        Json.encodeToString(JsonNodeSerializer, primitiveNode) shouldBe primitiveNode.toString()
        Json.encodeToString(JsonNodeSerializer, arrayNode) shouldBe arrayNode.toString()
        Json.encodeToString(JsonNodeSerializer, treeNode) shouldBe treeNode.toString()
    }

    "should parse from json successfully" {
        Json.decodeFromString(JsonNodeSerializer, "null") shouldBe JsonNull
        Json.decodeFromString(JsonNodeSerializer, treeNode.toString()) shouldBe treeNode
        Json.decodeFromString(JsonNodeSerializer, arrayNode.toString()) shouldBe arrayNode
        Json.decodeFromString(JsonNodeSerializer, primitiveNode.toString()) shouldBe primitiveNode
    }
})
