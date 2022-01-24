package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class JsonNodeAdapterTest : AnnotationSpec() {
    private val adapter = MoshiJsonAdapter()
    private val jsonString = """["bla",42,42.42,true,{"key":"value","other":null},null]"""

    @Test
    fun shouldSerializeAndDeserializeArrayNodeSuccessfully() {
        val arrayNode = ArrayNode(
            PrimitiveNode("bla"),
            PrimitiveNode(42),
            PrimitiveNode(42.42),
            PrimitiveNode(true),
            TreeNode("key" to PrimitiveNode("value"), "other" to JsonNull),
            JsonNull
        )

        adapter.toJson(arrayNode) shouldBe jsonString
        adapter.fromJson<JsonNode>(jsonString) shouldBe arrayNode
    }
}
