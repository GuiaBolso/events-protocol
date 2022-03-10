package br.com.guiabolso.events.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class PrimitiveNodeExtensionsTest : StringSpec({

    "int extension function should successfully convert from primitive value" {
        PrimitiveNode(42).int shouldBe 42
    }

    "int extension function should throws when can't convert from primitive value" {
        shouldThrow<IllegalArgumentException> { PrimitiveNode("bla").int }
    }

    "intOrNull extension function should successfully convert from primitive value" {
        PrimitiveNode("42").intOrNull shouldBe 42
    }

    "intOrNull extension function should return null when can't convert from primitive value" {
        PrimitiveNode("blax").intOrNull shouldBe null
    }

    "long extension function should successfully convert from primitive value" {
        PrimitiveNode("42").long shouldBe 42
    }

    "long extension function should throws when can't convert from primitive value" {
        shouldThrow<IllegalArgumentException> { PrimitiveNode("bla").long }
    }

    "longOrNull extension function should successfully convert from primitive value" {
        PrimitiveNode("42").longOrNull shouldBe 42
    }

    "longOrNull extension function should return null when can't convert from primitive value" {
        PrimitiveNode("bla").longOrNull shouldBe null
    }

    "double extension function should successfully convert from primitive value" {
        PrimitiveNode("42.42").double shouldBe 42.42
    }

    "double extension function should throws when can't convert from primitive value" {
        shouldThrow<IllegalArgumentException> { PrimitiveNode("bla").double }
    }

    "doubleOrNull extension function should successfully convert from primitive value" {
        PrimitiveNode("42.42").doubleOrNull shouldBe 42.42
    }

    "doubleOrNull extension function should return null when can't convert from primitive value" {
        PrimitiveNode("bla").doubleOrNull shouldBe null
    }

    "boolean extension function should successfully convert from primitive value" {
        PrimitiveNode("true").boolean shouldBe true
        PrimitiveNode("false").boolean shouldBe false
    }

    "boolean extension function should throws when can't convert from primitive value" {
        listOf("True", "TRUE", " ", "FALSE", "False", "bla").forAll { invalidBoolean ->
            shouldThrow<IllegalArgumentException> { PrimitiveNode(invalidBoolean).boolean }
        }
    }

    "booleanOrNull extension function should successfully convert from primitive value" {
        PrimitiveNode("true").booleanOrNull shouldBe true
        PrimitiveNode("false").booleanOrNull shouldBe false
    }

    "booleanOrNull extension function should return null when can't convert from primitive value" {
        PrimitiveNode("bla").booleanOrNull shouldBe null
    }

    "stringOrNull should return null for JsonNull instance" {
        val number: Number? = null
        val node = PrimitiveNode(number)

        node.stringOrNull shouldBe null
    }

    "stringOrNull should return the primitive value when it's not a JsonNull instance" {
        PrimitiveNode(42).stringOrNull shouldBe "42"
        PrimitiveNode("42").stringOrNull shouldBe "42"
        PrimitiveNode(true).stringOrNull shouldBe "true"
        PrimitiveNode(false).stringOrNull shouldBe "false"
    }

    "PrimitiveNode function should return JsonNull singleton when the supplied value is null" {
        PrimitiveNode(null as? Number?) shouldBeSameInstanceAs JsonNull
        PrimitiveNode(null as? String?) shouldBeSameInstanceAs JsonNull
        PrimitiveNode(null as? Boolean?) shouldBeSameInstanceAs JsonNull
    }

    "PrimitiveNode function should create a JsonLiteral instance for non null primitive values" {
        PrimitiveNode(42).should {
            it.shouldBeInstanceOf<JsonLiteral>()
            it.value shouldBe "42"
        }

        PrimitiveNode("42").should {
            it.shouldBeInstanceOf<JsonLiteral>()
            it.value shouldBe "42"
        }

        PrimitiveNode(true).should {
            it.shouldBeInstanceOf<JsonLiteral>()
            it.value shouldBe "true"
        }
    }

})
