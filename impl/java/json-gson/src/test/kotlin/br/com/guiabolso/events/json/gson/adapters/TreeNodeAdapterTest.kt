package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.gson.jsonWriter
import br.com.guiabolso.events.json.gson.toJsonReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.StringWriter

class TreeNodeAdapterTest : StringSpec({
    val treeNode = TreeNode(
        "null" to JsonNull,
        "int" to PrimitiveNode(1),
        "boolean" to PrimitiveNode(true),
        "string" to PrimitiveNode("string"),
        "tree" to TreeNode("key" to PrimitiveNode("value")),
        "array" to ArrayNode(PrimitiveNode(1))
    )

    "should read from json successfully" {
        val reader = treeNode.toString().toJsonReader()
        TreeNodeAdapter.read(reader) shouldBe treeNode
    }

    "should write null TreeNode reference" {
        val output = StringWriter()
        TreeNodeAdapter.write(jsonWriter(output), null)

        output.toString() shouldBe "null"
    }

    "should write TreeNode successfully" {
        val output = StringWriter()
        TreeNodeAdapter.write(jsonWriter(output), treeNode)

        output.toString() shouldBe treeNode.toString()
    }

    "should throw for duplicate key on json" {
        val ex = shouldThrow<JsonDataException> {
            TreeNodeAdapter.read(""" {"any":"first", "any": "second"} """.toJsonReader())
        }
        ex.message shouldBe """JsonNode key 'any' has multiple values at path $.any, values "first" and "second""""
    }
})
