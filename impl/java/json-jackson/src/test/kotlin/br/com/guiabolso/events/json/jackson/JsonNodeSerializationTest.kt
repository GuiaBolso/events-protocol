package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.arrayNode
import br.com.guiabolso.events.json.primitiveNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.math.BigDecimal

private const val literalBoolean = "true"
private const val literalInt = "42"
private const val literalFloat = "42.3"
private const val literalBigDecimal = "4233333333333333333.333333333333333333"
private const val literalArray = """["nome",42,42.3,false]"""
private const val literalObject = """{"nome":"johnson","idade":42,"filhos":["ana","juquinha"]}"""

class JsonNodeSerializationTest : StringSpec({

    "deserialize boolean" {
        val result = testAdapter.fromJson(literalBoolean, JsonNode::class.java)

        result.shouldBeInstanceOf<PrimitiveNode>()
        result.isBoolean.shouldBeTrue()
        result.value shouldBe "true"
    }

    "serialize boolean" {
        testAdapter.toJson(JsonLiteral(true)) shouldBe literalBoolean
    }

    "deserialize number" {
        val result = testAdapter.fromJson(literalInt, JsonNode::class.java)

        result.shouldBeInstanceOf<PrimitiveNode>()
        result.isNumber.shouldBeTrue()
        result.value shouldBe literalInt

        val resultFloat = testAdapter.fromJson(literalFloat, JsonNode::class.java)

        resultFloat.shouldBeInstanceOf<PrimitiveNode>()
        resultFloat.isNumber.shouldBeTrue()
        resultFloat.value shouldBe literalFloat

        val resultBigDecimal = testAdapter.fromJson(literalBigDecimal, JsonNode::class.java)

        resultBigDecimal.shouldBeInstanceOf<PrimitiveNode>()
        resultBigDecimal.isNumber.shouldBeTrue()
        resultBigDecimal.value shouldBe literalBigDecimal
    }

    "serialize number" {
        testAdapter.toJson(JsonLiteral(42)) shouldBe literalInt
        testAdapter.toJson(JsonLiteral(42.3)) shouldBe literalFloat
        testAdapter.toJson(JsonLiteral(BigDecimal(literalBigDecimal))) shouldBe literalBigDecimal
    }

    "deserialize array" {
        val result = testAdapter.fromJson(literalArray, JsonNode::class.java)

        result.shouldBeInstanceOf<ArrayNode>()

        result.shouldForAll {
            it.shouldBeInstanceOf<PrimitiveNode>()
        }

        result[0].primitiveNode.isString.shouldBeTrue()
        result[1].primitiveNode.isNumber.shouldBeTrue()
        result[2].primitiveNode.isNumber.shouldBeTrue()
        result[3].primitiveNode.isBoolean.shouldBeTrue()
    }

    "serialize array" {
        val list = ArrayNode(
            mutableListOf(PrimitiveNode("nome"),
                PrimitiveNode(42),
                PrimitiveNode(42.3),
                PrimitiveNode(false)))

        testAdapter.toJson(list) shouldBe literalArray
    }

    "deserialize object" {
        val result = testAdapter.fromJson(literalObject, JsonNode::class.java)

        result.shouldBeInstanceOf<TreeNode>()

        result["nome"]!!.primitiveNode.isString.shouldBeTrue()
        result["idade"]!!.primitiveNode.isNumber.shouldBeTrue()
        result["filhos"]!!.arrayNode
            .toList().shouldContainInOrder(PrimitiveNode("ana"), PrimitiveNode("juquinha"))
    }

    "serialize object" {

        val obj = TreeNode(
            "nome" to PrimitiveNode("johnson"),
            "idade" to PrimitiveNode(42),
            "filhos" to ArrayNode(PrimitiveNode("ana"), PrimitiveNode("juquinha"))
        )

        testAdapter.toJson(obj) shouldBe literalObject
    }
})
