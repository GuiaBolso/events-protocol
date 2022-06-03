package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json

class PrimitiveNodeSerializerTest : StringSpec({

    "should encode a JsonNull" {
        Json.encodeToString(PrimitiveNodeSerializer, JsonNull) shouldBe "null"
    }

    "should encode a String literal" {
        Json.encodeToString(PrimitiveNodeSerializer, PrimitiveNode("string")) shouldBe "\"string\""
    }

    "should encode a Boolean literal" {
        Json.encodeToString(PrimitiveNodeSerializer, PrimitiveNode(true)) shouldBe "true"
    }

    "should encode numbers" {
        listOf(Long.MIN_VALUE, Long.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE).forAll {
            Json.encodeToString(PrimitiveNodeSerializer, PrimitiveNode(it)) shouldBe it.toString()
        }
    }

    "should decode a json null literal" {
        Json.decodeFromString(PrimitiveNodeSerializer, "null") shouldBe JsonNull
    }

    "should decode a json string literal" {
        Json.decodeFromString(PrimitiveNodeSerializer, "\"string\"") shouldBe PrimitiveNode("string")
    }

    "should decode a json Boolean literal" {
        Json.decodeFromString(PrimitiveNodeSerializer, "true") shouldBe PrimitiveNode(true)
    }

    "should decode numbers" {
        listOf(Long.MIN_VALUE, Long.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE).forAll {
            Json.decodeFromString(PrimitiveNodeSerializer, it.toString()) shouldBe PrimitiveNode(it)
        }
    }
})
