package br.com.guiabolso.events.json

import br.com.guiabolso.events.json.JsonAdapterProducer.mapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class JsonAdapterExtensionsTest : StringSpec({

    "fromJsonOrNull should parse successfully from string" {
        val data = mapper.fromJsonOrNull<Data>(""" {"content": "42"} """)
        data.shouldNotBeNull()
        data.content shouldBe "42"
    }

    "fromJsonOrNull should return null when cant parse from string" {
        mapper.fromJsonOrNull<Data>("null").shouldBeNull()
    }

    "fromJsonOrNull should parse successfully from JsonNode" {
        val jsonNode = TreeNode("content" to PrimitiveNode("42"))
        val data = mapper.fromJsonOrNull<Data>(jsonNode)
        data.shouldNotBeNull()
        data.content shouldBe "42"
    }

    "fromJsonOrNull should return null when cant parse from JsonNode" {
        mapper.fromJsonOrNull<Data>(JsonNull).shouldBeNull()
    }

    "fromJson should parse successfully from string" {
        mapper.fromJson<Data>(""" {"content": "42"} """).content shouldBe "42"
    }

    "fromJson should throws when can't parse from string" {
        shouldThrow<JsonDataException> { mapper.fromJson<Data>("null") }
    }

    "fromJson should parse successfully from JsonNode" {
        val jsonNode = TreeNode("content" to PrimitiveNode("42"))
        val data = mapper.fromJson<Data>(jsonNode)
        data.shouldNotBeNull()
        data.content shouldBe "42"
    }

    "fromJson should throws when can't parse from JsonNode" {
        shouldThrow<JsonDataException> { mapper.fromJson<Data>(JsonNull) }
    }
})

data class Data(val content: String)
