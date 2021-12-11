package br.com.guiabolso.events.json

import kotlin.collections.MutableMap.MutableEntry

typealias TreeEntry = MutableEntry<String, JsonNode>

sealed interface JsonNode {

    data class TreeNode(
        private val nodes: MutableMap<String, JsonNode> = mutableMapOf(),
    ) : JsonNode, Iterable<MutableEntry<String, JsonNode>> {

        constructor(vararg nodes: Pair<String, JsonNode>) : this(nodes.associateTo(mutableMapOf()) { it })

        operator fun get(key: String): JsonNode? = nodes[key]

        operator fun set(key: String, value: JsonNode) {
            nodes[key] = value
        }

        operator fun contains(key: String): Boolean = key in nodes

        val size: Int get() = nodes.size

        override fun iterator(): Iterator<TreeEntry> = nodes.iterator()
    }

    data class ArrayNode(
        private val elements: MutableList<JsonNode> = ArrayList(),
    ) : JsonNode, Iterable<JsonNode> {

        constructor(vararg nodes: JsonNode) : this(nodes.toMutableList())

        operator fun get(index: Int): JsonNode = elements[index]

        operator fun set(index: Int, value: JsonNode) {
            elements[index] = value
        }

        fun add(element: JsonNode) {
            elements.add(element)
        }

        val size: Int get() = elements.size

        override fun iterator(): Iterator<JsonNode> = elements.iterator()
    }

    sealed interface PrimitiveNode<T> : JsonNode {
        val value: T

        data class StringNode(override val value: String) : PrimitiveNode<String> {
            override fun toString(): String = value
        }

        data class BooleanNode(override val value: Boolean) : PrimitiveNode<Boolean> {
            override fun toString(): String {
                return value.toString()
            }
        }

        sealed class AbstractNumber<T : Number> : PrimitiveNode<T> {

            override fun toString(): String {
                return value.toString()
            }
        }

        data class NumberNode(override val value: Number) : AbstractNumber<Number>()
        data class ByteNode(override val value: Byte) : AbstractNumber<Byte>()
        data class ShortNode(override val value: Short) : AbstractNumber<Short>()
        data class IntNode(override val value: Int) : AbstractNumber<Int>()
        data class LongNode(override val value: Int) : AbstractNumber<Int>()
        data class DoubleNode(override val value: Int) : AbstractNumber<Int>()
    }

    object JsonNull : JsonNode
}
