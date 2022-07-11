package br.com.guiabolso.events.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArrayNodeTest {
    private val entries = mutableListOf<JsonNode>(
        JsonNull,
        PrimitiveNode("42"),
        PrimitiveNode(42),
        PrimitiveNode(42.42),
        PrimitiveNode(true),
        TreeNode("string" to PrimitiveNode("any string")),
        ArrayNode(JsonNull, PrimitiveNode("42"))
    )

    @Test
    fun toStringShouldGenerateValidJson() {
        val arrayNode = ArrayNode(entries)
        val json = """[null,"42",42,42.42,true,{"string":"any string"},[null,"42"]]"""

        assertEquals(json, arrayNode.toString())
        assertEquals("[]", ArrayNode().toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val firstArrayNode = ArrayNode(entries)

        assertFalse(firstArrayNode == Any())

        val otherArrayNode = ArrayNode(entries)
        assertTrue(firstArrayNode == otherArrayNode)
        assertEquals(firstArrayNode.hashCode(), otherArrayNode.hashCode())
    }
}
