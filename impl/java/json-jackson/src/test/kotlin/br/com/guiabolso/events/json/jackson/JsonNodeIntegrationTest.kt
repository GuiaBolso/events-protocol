package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

private const val literalBoolean = "true"

class JsonNodeIntegrationTest : StringSpec({

    "deserialize boolean" {
        val result = testAdapter.fromJson(literalBoolean, JsonNode::class.java)

        result.shouldBeInstanceOf<PrimitiveNode>()
        result.isBoolean.shouldBeTrue()
    }

    "serialize boolean" {
        testAdapter.toJson(JsonLiteral(true)) shouldBe literalBoolean
    }
})