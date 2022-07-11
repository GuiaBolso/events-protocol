package br.com.guiabolso.events.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TreeNodeTest {
    private val entries = mutableMapOf(
        "nullNode" to JsonNull,
        "string" to PrimitiveNode("bla"),
        "int" to PrimitiveNode(42),
        "float" to PrimitiveNode(42.42),
        "boolean" to PrimitiveNode(true),
        "tab" to PrimitiveNode("\t"),
        "\r" to PrimitiveNode("cr"),
        "arrayNode" to ArrayNode(PrimitiveNode("string"), JsonNull),
        "treeNode" to TreeNode("string" to PrimitiveNode("42"), "null" to JsonNull)
    )

    @Test
    fun toStringShouldGenerateValidJson() {

        val treeNode = TreeNode(entries)

        val json =
            """{"nullNode":null,"string":"bla","int":42,"float":42.42,"boolean":true,"tab":"\t","\r":"cr","arrayNode":""" +
                    """["string",null],"treeNode":{"string":"42","null":null}}"""

        assertEquals(json, treeNode.toString())
        assertEquals("{}", TreeNode().toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val firstTreeNode = TreeNode(entries)
        val otherTreeNode = TreeNode(entries)

        assertFalse(firstTreeNode == Any())
        assertTrue(firstTreeNode == otherTreeNode)
        assertEquals(firstTreeNode.hashCode(), otherTreeNode.hashCode())
    }
}
