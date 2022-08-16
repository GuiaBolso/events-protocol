package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.gson.jsonWriter
import br.com.guiabolso.events.json.gson.toJsonReader
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.io.StringWriter

class JsonNodeAdapterTest : StringSpec({

    "should read from json successfully" {
        listOf(
            JsonNull,
            PrimitiveNode(42),
            TreeNode("key" to ArrayNode()),
            ArrayNode(PrimitiveNode("string"))
        ).forAll { source ->
            JsonNodeAdapter.read(source.toString().toJsonReader()) shouldBe source
        }
    }

    "should write JsonNode successfully" {
        listOf(
            JsonNull,
            PrimitiveNode(42),
            TreeNode("key" to ArrayNode()),
            ArrayNode(PrimitiveNode("string"))
        ).forAll { source ->
            val output = StringWriter()
            JsonNodeAdapter.write(jsonWriter(output), source)

            output.toString() shouldBe source.toString()
        }
    }

    "should safely write null JsonNode" {
        val output = StringWriter()
        JsonNodeAdapter.write(jsonWriter(output), null)

        output.toString() shouldBe "null"
    }
})
