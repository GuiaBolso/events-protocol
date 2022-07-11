package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.moshi
import com.squareup.moshi.JsonDataException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TreeNodeAdapterTest : StringSpec({
    val treeNodeAdapter = TreeNodeAdapter(moshi)
    val json = """{"one":1,"boolean":true,"nullable":null,"array":["string"]}"""
    val treeNode = TreeNode(
        "one" to PrimitiveNode(1),
        "boolean" to PrimitiveNode(true),
        "nullable" to JsonNull,
        "array" to ArrayNode(PrimitiveNode("string"))
    )

    "should deserialize a TreeNode successfully" {
        treeNodeAdapter.fromJson(json) shouldBe treeNode
    }

    "should throw for duplicate key on json" {
        val invalidJson = """ {"any":"first", "any": "second"} """
        val ex = shouldThrow<JsonDataException> {
            treeNodeAdapter.fromJson(invalidJson)
        }
        ex.message shouldBe """JsonNode key 'any' has multiple values at path $.any, values "first" and "second""""
    }

    "should write null values into writer" {
        treeNodeAdapter.serializeNulls().toJson(null) shouldBe "null"
    }

    "should write tree node successfully" {
        treeNodeAdapter.serializeNulls().toJson(treeNode) shouldBe json
    }
})
