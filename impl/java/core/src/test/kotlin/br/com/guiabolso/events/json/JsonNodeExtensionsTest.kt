package br.com.guiabolso.events.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class JsonNodeExtensionsTest : StringSpec({

    "primitiveNode extension should successfully cast" {
        val jsonNode: JsonNode = PrimitiveNode("a string")
        jsonNode.primitiveNode.shouldBeInstanceOf<PrimitiveNode>()
    }

    "primitiveNode extension should throws when can't cast successfully" {
        val ex = shouldThrow<IllegalArgumentException> { ArrayNode().primitiveNode }
        ex.message shouldContain "JsonNode is not a ${PrimitiveNode::class.java}"
    }

    "primitiveNodeOrNull extension should successfully cast" {
        val jsonNode: JsonNode = PrimitiveNode("a string")
        jsonNode.primitiveNodeOrNull.shouldBeInstanceOf<PrimitiveNode>()
    }

    "primitiveNodeOrNull extension should return null when can't cast" {
        ArrayNode().primitiveNodeOrNull.shouldBeNull()
    }

    "jsonNull extension should successfully cast" {
        val jsonNode: JsonNode = JsonNull
        jsonNode.jsonNull.shouldBeSameInstanceAs(JsonNull)
    }

    "jsonNull extension should throws when can't cast successfully" {
        val ex = shouldThrow<IllegalArgumentException> { ArrayNode().jsonNull }
        ex.message shouldContain "JsonNode is not a ${JsonNull::class.java}"
    }

    "treeNode extension should successfully cast" {
        val jsonNode: JsonNode = TreeNode()
        jsonNode.treeNode.shouldBeInstanceOf<TreeNode>()
    }

    "treeNode extension should throws when can't cast successfully" {
        val ex = shouldThrow<IllegalArgumentException> { ArrayNode().treeNode }
        ex.message shouldContain "JsonNode is not a ${TreeNode::class.java}"
    }

    "treeNodeOrNull extension should successfully cast" {
        val jsonNode: JsonNode = TreeNode()
        jsonNode.treeNodeOrNull.shouldBeInstanceOf<TreeNode>()
    }

    "treeNodeOrNull extension should return null when can't cast" {
        ArrayNode().treeNodeOrNull.shouldBeNull()
    }

    "arrayNode extension should successfully cast" {
        val jsonNode: JsonNode = ArrayNode()
        jsonNode.arrayNode.shouldBeInstanceOf<ArrayNode>()
    }

    "arrayNode extension should throws when can't cast successfully" {
        val ex = shouldThrow<IllegalArgumentException> { TreeNode().arrayNode }
        ex.message shouldContain "JsonNode is not a ${ArrayNode::class.java}"
    }

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

    "should copy the entire node graph" {
        val primitive = PrimitiveNode("a")
        val tree = TreeNode("a" to primitive)
        val arrayNode = ArrayNode(tree)

        val copy: ArrayNode = arrayNode.deepCopy()

        copy shouldNotBeSameInstanceAs arrayNode
        copy.first().should {
            it shouldNotBeSameInstanceAs tree
            it.treeNode["a"] shouldBeSameInstanceAs primitive
        }
    }
})
