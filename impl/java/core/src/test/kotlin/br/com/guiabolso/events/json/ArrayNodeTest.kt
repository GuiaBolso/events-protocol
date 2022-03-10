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
        PrimitiveNode(true)
    )

    @Test
    fun toStringShouldGenerateValidJson() {
        val arrayNode = ArrayNode(entries)
        val json = """[null,"42",42,42.42,true]"""

        assertEquals(json, arrayNode.toString())
        assertEquals("[]", ArrayNode().toString())
    }

    @Test
    fun testEqualsAndHashCode() {
        val firstArrayNode = ArrayNode(entries)

        assertFalse(firstArrayNode.equals(null))
        assertFalse(firstArrayNode == Any())

        val otherArrayNode = ArrayNode(entries)
        assertTrue(firstArrayNode == otherArrayNode)
        assertEquals(firstArrayNode.hashCode(), otherArrayNode.hashCode())
    }
}
