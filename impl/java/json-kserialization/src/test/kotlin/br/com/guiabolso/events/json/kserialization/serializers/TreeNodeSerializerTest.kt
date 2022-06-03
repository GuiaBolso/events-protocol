package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class TreeNodeSerializerTest : StringSpec({
    val json = """{"one":1,"boolean":true,"nullable":null,"array":["string"]}"""
    val treeNode = TreeNode(
        "one" to PrimitiveNode(1),
        "boolean" to PrimitiveNode(true),
        "nullable" to JsonNull,
        "array" to ArrayNode(PrimitiveNode("string"))
    )

    "should decode a TreeNode successfully" {
        Json.decodeFromString(TreeNodeSerializer, json) shouldBe treeNode
    }

    "should encode TreeNode successfully" {
        Json.encodeToString(TreeNodeSerializer, treeNode) shouldBe json
    }
})
