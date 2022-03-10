package br.com.guiabolso.events.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PrimitiveNodeTest {

    @Test
    fun toStringShouldGenerateValidJsonString() {
        assertEquals("42", PrimitiveNode(42).toString())
        assertEquals("42.42", PrimitiveNode(42.42).toString())
        assertEquals("true", PrimitiveNode(true).toString())
        assertEquals("\"string\"", PrimitiveNode("string").toString())
        assertEquals("null", JsonNull.toString())
    }
}
