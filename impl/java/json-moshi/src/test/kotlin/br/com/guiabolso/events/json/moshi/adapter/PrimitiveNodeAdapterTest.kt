package br.com.guiabolso.events.json.moshi.br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.boolean
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.int
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import br.com.guiabolso.events.json.primitiveNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class PrimitiveNodeAdapterTest : StringSpec({
    val adapter = MoshiJsonAdapter()

    "should quote string literal" {
        adapter.toJson(PrimitiveNode("bla")) shouldBe "\"bla\""
    }

    "should serialize boolean" {
        adapter.toJson(PrimitiveNode(true)) shouldBe "true"
    }

    "should serialize number" {
        adapter.toJson(PrimitiveNode(Long.MIN_VALUE)) shouldBe Long.MIN_VALUE.toString()
        adapter.toJson(PrimitiveNode(Long.MAX_VALUE)) shouldBe Long.MAX_VALUE.toString()

        val minDoubleString = adapter.toJson(PrimitiveNode(Double.MIN_VALUE))
        minDoubleString.toDouble() shouldBe Double.MIN_VALUE

        val maxDoubleString = adapter.toJson(PrimitiveNode(Double.MAX_VALUE))
        maxDoubleString.toDouble() shouldBe Double.MAX_VALUE
    }

    "should serialize null" {
        adapter.toJson(JsonNull) shouldBe "null"
    }

    "should deserialize string literal to PrimitiveNode" {
        val node = adapter.fromJson<PrimitiveNode>(""" "primitiveString"  """)
        node.shouldBeInstanceOf<PrimitiveNode>()
        node.isString shouldBe true
        node.isNumber shouldBe false
        node.isBoolean shouldBe false
        node.value shouldBe "primitiveString"
    }

    "should deserialize number to  PrimitiveNode" {
        val node = adapter.fromJson<PrimitiveNode>("1234")
        node.isNumber shouldBe true
        node.isString shouldBe false
        node.isBoolean shouldBe false
        node.int shouldBe 1234
    }

    "should deserialize boolean to  PrimitiveNode" {
        val node = adapter.fromJson<PrimitiveNode>("false")
        node.isBoolean shouldBe true
        node.isNumber shouldBe false
        node.isString shouldBe false

        node.boolean shouldBe false
    }

    "should deserialize null to JsonNull primitive node" {
        val node = adapter.fromJson<ArrayNode>(""" [null] """).first().primitiveNode
        node shouldBeSameInstanceAs JsonNull
        node.isString shouldBe false
        node.isBoolean shouldBe false
        node.isNumber shouldBe false
        node.value shouldBe "null"
    }
})
