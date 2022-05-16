package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.gson.jsonWriter
import br.com.guiabolso.events.json.gson.toJsonReader
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import java.io.StringWriter

class PrimitiveNodeAdapterTest : StringSpec({

    "should read from json successfully" {
        listOf(
            JsonNull,
            PrimitiveNode(42),
            PrimitiveNode("42"),
            PrimitiveNode(true),
            PrimitiveNode(false),
        ).forAll { source ->
            JsonNodeAdapter.read(source.toString().toJsonReader()) shouldBe source
        }
    }

    "should write PrimitiveNode successfully" {
        listOf(
            JsonNull,
            PrimitiveNode(42),
            PrimitiveNode("42"),
            PrimitiveNode(true),
            PrimitiveNode(false),
        ).forAll { source ->
            val output = StringWriter()
            JsonNodeAdapter.write(jsonWriter(output), source)

            output.toString() shouldBe source.toString()
        }
    }
})
