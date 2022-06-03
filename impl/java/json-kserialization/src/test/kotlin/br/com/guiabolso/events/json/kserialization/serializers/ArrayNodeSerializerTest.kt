package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class ArrayNodeSerializerTest : StringSpec({
    val json = """[null,"bla",42,42.42,true,{"key":"value"}]"""
    val arrayNode = ArrayNode(
        JsonNull,
        PrimitiveNode("bla"),
        PrimitiveNode(42),
        PrimitiveNode(42.42),
        PrimitiveNode(true),
        TreeNode("key" to PrimitiveNode("value"))
    )

    "should serialize array node successfully" {
        Json.encodeToString(JsonNodeSerializer, arrayNode) shouldBe json
    }

    "should deserialize from json successfully" {
        Json.decodeFromString(ArrayNodeSerializer, json) shouldBe arrayNode
    }
})
