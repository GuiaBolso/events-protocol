package br.com.guiabolso.events.json.moshi.adapter


import br.com.guiabolso.events.json.JsonNode.ArrayNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.moshi.DataWrapper
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class NumberNodeAdapterTest : AnnotationSpec() {
    private val nodeAdapter = MoshiJsonAdapter()

    @Test
    fun shouldDeserializeIntNumber() {
        assertDeserializationOfNumbers(Int.MIN_VALUE, Int.MAX_VALUE)
    }

    @Test
    fun shouldDeserializeLongNumber() {
        assertDeserializationOfNumbers(Long.MIN_VALUE, Long.MAX_VALUE)
    }

    @Test
    fun shouldDeserializeDoubleNumber() {
        assertDeserializationOfNumbers(Double.MIN_VALUE, Double.MAX_VALUE)
    }

    private inline fun <reified T : Number> assertDeserializationOfNumbers(firstNumber: T, secondNumber: T) {
        val dataWrapper = nodeAdapter.fromJson<DataWrapper>("""{"data": [$firstNumber, $secondNumber ]}""")
        dataWrapper.shouldNotBeNull()
        dataWrapper.data.should { node ->
            node.shouldBeInstanceOf<ArrayNode>()
            node shouldHaveSize 2

            val first = node.first()
            first.shouldBeInstanceOf<NumberNode>()
            first.value.should { number ->
                number.shouldBeInstanceOf<T>()
                number shouldBe firstNumber
            }

            val second = node.last()
            second.shouldBeInstanceOf<NumberNode>()
            second.value.should { number ->
                number.shouldBeInstanceOf<T>()
                number shouldBe secondNumber
            }
        }
    }

    @Test
    fun shouldSerializeIntNumber() {
        assertSerializationOfNumbers(Int.MIN_VALUE, Int.MAX_VALUE)
    }

    @Test
    fun shouldSerializeLongNumber() {
        assertSerializationOfNumbers(Long.MIN_VALUE, Long.MAX_VALUE)
    }

    @Test
    fun shouldSerializeDoubleNumber() {
        assertSerializationOfNumbers(Double.MIN_VALUE, Double.MAX_VALUE)
    }

    private inline fun <reified T : Number> assertSerializationOfNumbers(firstNumber: T, secondNumber: T) {
        val numbers = ArrayNode(
            NumberNode(firstNumber),
            NumberNode(secondNumber)
        )
        nodeAdapter.toJson(DataWrapper(numbers)) shouldBe """{"data":[$firstNumber,$secondNumber]}"""
    }
}
