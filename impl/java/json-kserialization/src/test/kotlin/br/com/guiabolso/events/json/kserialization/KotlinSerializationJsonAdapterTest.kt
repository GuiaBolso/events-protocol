package br.com.guiabolso.events.json.kserialization

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.kserialization.helpers.Sample
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

class KotlinSerializationJsonAdapterTest : StringSpec({
    val jsonString =
        """{"list":[42.42],"string":"string","int":42,"boolean":false,""" +
                """"map":{"bla":"bla"},"any":null}"""

    val sample = Sample(
        int = 42,
        any = null,
        boolean = false,
        string = "string",
        map = mapOf("bla" to "bla"),
        list = listOf(42.42)
    )

    val jsonNode = TreeNode(
        "int" to PrimitiveNode(42),
        "any" to JsonNull,
        "boolean" to PrimitiveNode(false),
        "string" to PrimitiveNode("string"),
        "map" to TreeNode("bla" to PrimitiveNode("bla")),
        "list" to ArrayNode(PrimitiveNode(42.42))
    )

    val adapter = KotlinSerializationJsonAdapter()

    "should serialize and deserialize all JsonNode types" {
        listOf(
            JsonNull,
            PrimitiveNode(1),
            PrimitiveNode(true),
            PrimitiveNode("string"),
            ArrayNode(PrimitiveNode(1)),
            TreeNode("element" to PrimitiveNode("any"))
        ).forEach { jsonNode ->
            val json = adapter.toJson(jsonNode)
            json shouldBe jsonNode.toString()
            adapter.fromJson<JsonNode>(json) shouldBe jsonNode
        }
    }

    "should serialize object successfully" {
        adapter.toJson(sample) shouldBe jsonString
    }

    "should serialize array successfully" {
        adapter.toJson(arrayOf(1, 2, 3, 4)) shouldBe "[1,2,3,4]"
    }

    "should deserialize successfully using type argument" {
        adapter.fromJson<Sample>(jsonString, typeOf<Sample>().javaType) shouldBe sample
    }

    "should deserialize successfully using class argument" {
        adapter.fromJson(jsonString, Sample::class.java) shouldBe sample
    }

    "should deserialize successfully using JsonNode and type argument" {
        adapter.fromJson<Sample>(jsonNode, typeOf<Sample>().javaType) shouldBe sample
    }

    "should deserialize successfully using JsonNode and class argument" {
        adapter.fromJson(jsonNode, Sample::class.java) shouldBe sample
    }

    "should return JsonNull instance when call toJsonTree with null " {
        adapter.toJsonTree(null) shouldBeSameInstanceAs JsonNull
    }

    "should returns self when call toJsonTree with JsonNode argument" {
        adapter.toJsonTree(jsonNode) shouldBeSameInstanceAs jsonNode
    }

    "should create a TreeNode" {
        adapter.toJsonTree(sample) shouldBe jsonNode
    }
})
