package br.com.guiabolso.events.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class JsonNodeExtensionsTest : StringSpec({

    "primitiveNode extension should successfully cast" {
        val jsonNode: JsonNode = PrimitiveNode("a string")
        jsonNode.primitiveNode.shouldBeInstanceOf<PrimitiveNode>()
        jsonNode.primitiveNodeOrNull.shouldBeInstanceOf<PrimitiveNode>()
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
        jsonNode.treeNodeOrNull.shouldBeInstanceOf<TreeNode>()
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

})
