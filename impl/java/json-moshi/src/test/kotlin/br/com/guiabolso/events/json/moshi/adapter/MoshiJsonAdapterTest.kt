package br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.ArrayNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.PrimitiveNode
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
        "int" to PrimitiveNode(42),
        "any" to JsonNull,
        "boolean" to PrimitiveNode(false),
        "string" to PrimitiveNode("string"),
        "map" to TreeNode("bla" to PrimitiveNode("bla")),
        "list" to ArrayNode(
            PrimitiveNode(42.42),
            TreeNode("nested" to ArrayNode()),
            PrimitiveNode(true),
            PrimitiveNode("string")
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
