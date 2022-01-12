package br.com.guiabolso.events.json

sealed interface JsonNode {

    data class TreeNode(
        private val nodes: MutableMap<String, JsonNode> = mutableMapOf()
    ) : JsonNode, MutableMap<String, JsonNode> by nodes {

        constructor(vararg nodes: Pair<String, JsonNode>) : this(nodes.associateTo(mutableMapOf()) { it })
    }

    data class ArrayNode(
        private val elements: MutableList<JsonNode> = ArrayList()
    ) : JsonNode, MutableList<JsonNode> by elements {

        constructor(vararg elements: JsonNode) : this(elements.toMutableList())
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

        data class NumberNode(override val value: Number) : PrimitiveNode<Number> {
            override fun toString() = value.toString()
        }
    }

    object JsonNull : JsonNode {
        override fun toString(): String {
            return "null"
        }
    }
}
