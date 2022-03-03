package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TreeNodeAdapterTest : StringSpec({
    val adapter = MoshiJsonAdapter()

    "serialize and then deserialize a array node back again should give the same input source" {
        val arrayNode = ArrayNode(
            JsonNull,
            PrimitiveNode("bla"),
            PrimitiveNode(42),
            PrimitiveNode(42.42),
            PrimitiveNode(true),
            TreeNode("key" to PrimitiveNode("value"), "other" to JsonNull)
        )

        val json = adapter.toJson(arrayNode)
        adapter.fromJson<ArrayNode>(json) shouldBe arrayNode
    }

    "serialize and then deserialize a tree node back again should give the same input source" {
        val treeNode = TreeNode(
            "key" to PrimitiveNode("value"),
            "other" to TreeNode("array" to ArrayNode()),
            "nullNode" to JsonNull
        )

        val json = adapter.toJson(treeNode)
        adapter.fromJson<TreeNode>(json) shouldBe treeNode
    }
})
