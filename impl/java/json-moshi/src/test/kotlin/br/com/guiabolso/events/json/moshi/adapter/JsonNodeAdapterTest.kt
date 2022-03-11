package br.com.guiabolso.events.json.moshi.br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.adapter.JsonNodeAdapter
import br.com.guiabolso.events.json.moshi.moshi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonNodeAdapterTest : StringSpec({
    val adapter = JsonNodeAdapter(moshi = moshi)

    val primitiveNode = PrimitiveNode("value")
    val arrayNode = ArrayNode(PrimitiveNode("value"))
    val treeNode = TreeNode("key" to PrimitiveNode("value"))

    "should write json node as json successfully" {
        adapter.toJson(null) shouldBe "null"
        adapter.toJson(primitiveNode) shouldBe primitiveNode.toString()
        adapter.toJson(arrayNode) shouldBe arrayNode.toString()
        adapter.toJson(treeNode) shouldBe treeNode.toString()
    }

    "should parse from json successfully" {
        adapter.fromJson("null") shouldBe JsonNull
        adapter.fromJson(treeNode.toString()) shouldBe treeNode
        adapter.fromJson(arrayNode.toString()) shouldBe arrayNode
        adapter.fromJson(primitiveNode.toString()) shouldBe primitiveNode
    }
})
