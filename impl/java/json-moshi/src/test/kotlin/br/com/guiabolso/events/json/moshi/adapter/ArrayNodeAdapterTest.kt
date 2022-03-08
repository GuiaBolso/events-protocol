package br.com.guiabolso.events.json.moshi.br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.adapter.ArrayNodeAdapter
import br.com.guiabolso.events.json.moshi.jsonReader
import br.com.guiabolso.events.json.moshi.jsonWriter
import br.com.guiabolso.events.json.moshi.moshi
import br.com.guiabolso.events.json.moshi.toJson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream

class ArrayNodeAdapterTest : StringSpec({
    val adapter = ArrayNodeAdapter(moshi = moshi)

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
        val output = ByteArrayOutputStream()
        output.jsonWriter().use { adapter.toJson(it, arrayNode) }
        output.toJson() shouldBe json
    }

    "should write null array node" {
        val output = ByteArrayOutputStream()
        output.jsonWriter().use { adapter.toJson(it, null) }
        output.toJson() shouldBe "null"
    }

    "should deserialize from json successfully" {
        adapter.fromJson(json.jsonReader()) shouldBe arrayNode
    }
})
