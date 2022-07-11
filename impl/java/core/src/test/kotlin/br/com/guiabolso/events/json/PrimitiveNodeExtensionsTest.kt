package br.com.guiabolso.events.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class PrimitiveNodeExtensionsTest : StringSpec({

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
