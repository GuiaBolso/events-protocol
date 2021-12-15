package br.com.guiabolso.events.json.moshi.adapter

import StringDataWrapper
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.moshi.noNullAdapterFor
import moshi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StringNodeAdapterTest {
    val stringNode = StringNode("string")

    @Test
    fun shouldCreateStringNodeForString() {
        val wrapper = moshi.noNullAdapterFor<StringDataWrapper>().fromJson("""{"data":"string"}""")
        assertEquals(stringNode, wrapper?.data)
    }

    @Test
    fun shouldReturnNullForJsonNullValue() {
        val wrapper = moshi.noNullAdapterFor<StringDataWrapper>().fromJson("""{"data":null}""")
        assertNotNull(wrapper)
        assertNull(wrapper?.data)
    }

    @Test
    fun shouldWriteTheStringNodeValueInTheWriter() {
        val data = StringDataWrapper(stringNode)
        assertEquals("""{"data":"string"}""", moshi.noNullAdapterFor<StringDataWrapper>().toJson(data))
    }

    @Test
    fun shouldWriteNullValueForNullStringNode() {
        assertEquals("""{"data":null}""", moshi.noNullAdapterFor<StringDataWrapper>().toJson(StringDataWrapper(null)))
    }
}
