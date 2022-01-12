package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNode.ArrayNode
import br.com.guiabolso.events.json.JsonNode.JsonNull
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.BooleanNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.NumberNode
import br.com.guiabolso.events.json.JsonNode.PrimitiveNode.StringNode
import br.com.guiabolso.events.json.JsonNode.TreeNode
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
            StringNode("bla"),
            NumberNode(42),
            NumberNode(42.42),
            BooleanNode(true),
            TreeNode("key" to StringNode("value"), "other" to JsonNull),
            JsonNull
        )

        adapter.toJson(arrayNode) shouldBe jsonString
        adapter.fromJson<JsonNode>(jsonString) shouldBe arrayNode
    }
}
