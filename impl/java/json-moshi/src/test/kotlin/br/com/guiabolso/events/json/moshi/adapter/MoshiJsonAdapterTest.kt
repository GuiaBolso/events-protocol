package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.JsonNode.ArrayNode
import br.com.guiabolso.events.json.JsonNode.JsonNull
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.JsonPrimitive
import br.com.guiabolso.events.json.fromJson
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import br.com.guiabolso.events.json.moshi.Sample
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class MoshiJsonAdapterTest : AnnotationSpec() {
    private val jsonString = """
        {"list":[42.42,{"nested":[]},true,"string"],"string":"string","int":42,"boolean":false,"map":{"bla":"bla"}}
        """.trimIndent()

    private val sample = Sample(
        int = 42,
        any = null,
        boolean = false,
        string = "string",
        map = mapOf("bla" to "bla"),
        list = listOf(42.42, mapOf("nested" to emptyList<Any>()), true, "string")
    )

    private val jsonNode = TreeNode(
        "int" to JsonPrimitive(42),
        "any" to JsonNull,
        "boolean" to JsonPrimitive(false),
        "string" to JsonPrimitive("string"),
        "map" to TreeNode("bla" to JsonPrimitive("bla")),
        "list" to ArrayNode(
            JsonPrimitive(42.42),
            TreeNode("nested" to ArrayNode()),
            JsonPrimitive(true),
            JsonPrimitive("string")
        )
    )

    private val adapter = MoshiJsonAdapter()

    @Test
    fun shouldSerializeObjectSuccessfully() {
        adapter.toJson(sample) shouldBe jsonString
    }

    @Test
    fun shouldSerializeArraySuccessfully() {
        adapter.toJson(arrayOf(1, 2, 3, 4)) shouldBe "[1,2,3,4]"
    }

    @Test
    fun shouldDeserializeSuccessfullyUsingTypeArgument() {
        adapter.fromJson<Sample?>(jsonString) shouldBe sample
    }

    @Test
    fun shouldDeserializeSuccessfullyUsingClassArgument() {
        adapter.fromJson(jsonString, Sample::class.java) shouldBe sample
    }

    @Test
    fun shouldDeserializeSuccessfullyUsingJsonNodeAndTypeArgument() {
        adapter.fromJson<Sample>(jsonNode) shouldBe sample
    }

    @Test
    fun shouldDeserializeSuccessfullyUsingJsonNodeAndClassArgument() {
        adapter.fromJson(jsonNode, Sample::class.java) shouldBe sample
    }

    @Test
    fun shouldReturnJsonNullInstanceOnCallToJsonTreeOnNullObject() {
        adapter.toJsonTree(null) shouldBeSameInstanceAs JsonNull
    }

    @Test
    fun shouldReturnsJsonNodeItSelfOnCallToJsonTreeWithJsonNodeArgument() {
        val jsonNode = TreeNode()
        adapter.toJsonTree(jsonNode) shouldBeSameInstanceAs jsonNode
    }

    @Test
    fun shouldCreateAJsonNodeTree() {
        val toJsonTree = adapter.toJsonTree(sample)
        toJsonTree shouldBe jsonNode
    }
}
