package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.gson.jsonWriter
import br.com.guiabolso.events.json.gson.toJsonReader
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.StringWriter

class ArrayNodeAdapterTest : StringSpec({
    val arrayNode = ArrayNode(
        JsonNull,
        PrimitiveNode(1),
        PrimitiveNode(true),
        PrimitiveNode("string"),
        TreeNode("key" to PrimitiveNode("value")),
        ArrayNode(PrimitiveNode(1))
    )

    "should read from json successfully" {
        val reader = arrayNode.toString().toJsonReader()
        ArrayNodeAdapter.read(reader) shouldBe arrayNode
    }

    "should write null ArrayNode reference" {
        val output = StringWriter()
        ArrayNodeAdapter.write(jsonWriter(output), null)

        output.toString() shouldBe "null"
    }

    "should write ArrayNode successfully" {
        val output = StringWriter()
        ArrayNodeAdapter.write(jsonWriter(output), arrayNode)

        output.toString() shouldBe arrayNode.toString()
    }
})
