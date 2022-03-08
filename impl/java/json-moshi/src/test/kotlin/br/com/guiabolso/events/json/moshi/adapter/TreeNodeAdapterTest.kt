package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.jsonReader
import br.com.guiabolso.events.json.moshi.jsonWriter
import br.com.guiabolso.events.json.moshi.moshi
import br.com.guiabolso.events.json.moshi.toJson
import com.squareup.moshi.JsonDataException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream

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
        treeNodeAdapter.fromJson(json.jsonReader()) shouldBe treeNode
    }

    "should throw for duplicate key on json" {
        val invalidJson = """ {"any":"first", "any": "second"} """
        val ex = shouldThrow<JsonDataException> {
            treeNodeAdapter.fromJson(invalidJson.jsonReader())
        }
        ex.message shouldBe """JsonNode key 'any' has multiple values at path $.any, values "first" and "second""""
    }

    "should write null values" {
        val output = ByteArrayOutputStream()
        output.jsonWriter().use { treeNodeAdapter.toJson(it, null) }
        output.toJson() shouldBe "null"
    }

    "should write tree node successfully" {
        val output = ByteArrayOutputStream()
        output.jsonWriter().use { treeNodeAdapter.toJson(it, treeNode) }
        output.toJson() shouldBe json
    }

})
