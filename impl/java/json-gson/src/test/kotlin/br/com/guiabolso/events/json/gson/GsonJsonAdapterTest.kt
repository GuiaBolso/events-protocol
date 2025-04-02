package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.fromJson
import com.google.gson.JsonSyntaxException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class GsonJsonAdapterTest : StringSpec({
    val jsonString =
        """{"list":[42.42,{"nested":[]},true,"string"],"string":"string","int":42,"boolean":false,""" +
            """"map":{"bla":"bla"},"any":null}"""

    val sample = Sample(
        int = 42,
        any = null,
        boolean = false,
        string = "string",
        map = mapOf("bla" to "bla"),
        list = listOf(42.42, mapOf("nested" to emptyList<Any>()), true, "string")
    )

    val jsonNode = TreeNode(
        "int" to PrimitiveNode(42),
        "any" to JsonNull,
        "boolean" to PrimitiveNode(false),
        "string" to PrimitiveNode("string"),
        "map" to TreeNode("bla" to PrimitiveNode("bla")),
        "list" to ArrayNode(
            PrimitiveNode(42.42),
            TreeNode("nested" to ArrayNode()),
            PrimitiveNode(true),
            PrimitiveNode("string")
        )
    )

    val adapter = GsonJsonAdapter()

    "should serialize object successfully" {
        adapter.toJson(sample) shouldBe jsonString
    }

    "should decode from input stream" {
        adapter.fromJson<Sample>(jsonString.byteInputStream()) shouldBe sample
    }

    "should successfully serialize nulls" {
        adapter.toJson(null) shouldBe "null"
    }

    "should serialize array successfully" {
        adapter.toJson(arrayOf(1, 2, 3, 4)) shouldBe "[1,2,3,4]"
    }

    "should deserialize successfully using type argument" {
        adapter.fromJson<Sample?>(jsonString) shouldBe sample
    }

    "should deserialize successfully using class argument" {
        adapter.fromJson(jsonString, Sample::class.java) shouldBe sample
    }

    "should deserialize successfully using JsonNode and type argument" {
        adapter.fromJson<Sample>(jsonNode) shouldBe sample
    }

    "should deserialize successfully using JsonNode and class argument" {
        adapter.fromJson(jsonNode, Sample::class.java) shouldBe sample
    }

    "should return JsonNull instance when call toJsonTree with null" {
        adapter.toJsonTree(null) shouldBeSameInstanceAs JsonNull
    }

    "should returns self when call toJsonTree with JsonNode argument" {
        adapter.toJsonTree(jsonNode) shouldBeSameInstanceAs jsonNode
    }

    "should create a TreeNode" {
        val toJsonTree = adapter.toJsonTree(sample)
        toJsonTree shouldBe jsonNode
    }

    "should wrap parse exception with JsonDataException" {
        val ex = shouldThrow<JsonDataException> { adapter.fromJson("{}`", Sample::class.java) }
        ex.message shouldContain "Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 4 path"
        ex.cause.should {
            it.shouldNotBeNull()
            it.shouldBeInstanceOf<JsonSyntaxException>()
        }
    }
})
