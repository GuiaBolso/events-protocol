package br.com.guiabolso.events.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TreeNodeTest {
    private val entries = mutableMapOf<String, JsonNode>(
        "nullNode" to JsonNull,
        "string" to PrimitiveNode("bla"),
        "int" to PrimitiveNode(42),
        "float" to PrimitiveNode(42.42),
        "boolean" to PrimitiveNode(true)
    )

    @Test
    fun toStringShouldGenerateValidJson() {

        val treeNode = TreeNode(entries)

        val json = """{"nullNode":null,"string":"bla","int":42,"float":42.42,"boolean":true}"""

        assertEquals(json, treeNode.toString())
        assertEquals("{}", TreeNode().toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val firstTreeNode = TreeNode(entries)
        val otherTreeNode = TreeNode(entries)

        assertTrue(firstTreeNode == otherTreeNode)
        assertEquals(firstTreeNode.hashCode(), otherTreeNode.hashCode())
    }
}
