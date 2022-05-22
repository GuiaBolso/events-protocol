package br.com.guiabolso.events.json

sealed class JsonNode

class TreeNode(
    private val nodes: MutableMap<String, JsonNode> = mutableMapOf()
) : JsonNode(), MutableMap<String, JsonNode> by nodes {

    constructor(vararg nodes: Pair<String, JsonNode>) : this(nodes.associateTo(mutableMapOf()) { it })

    override fun toString(): String {
        return nodes.entries.joinToString(separator = ",", prefix = "{", postfix = "}") { (key, jsonNode) ->
            buildString {
                append(JsonString.escape(key).toQuotedString())
                append(':')
                append(jsonNode)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        return nodes == (other as? TreeNode)?.nodes
    }

    override fun hashCode() = nodes.hashCode()
}

private fun String.toQuotedString() = "\"${this}\""

class ArrayNode(
    private val elements: MutableList<JsonNode> = ArrayList()
) : JsonNode(), MutableList<JsonNode> by elements {

    constructor(vararg elements: JsonNode) : this(elements.toMutableList())

    override fun hashCode(): Int = elements.hashCode()
    override fun equals(other: Any?): Boolean = elements == (other as? ArrayNode)?.elements
    override fun toString(): String = elements.joinToString(prefix = "[", postfix = "]", separator = ",")
}

sealed class PrimitiveNode : JsonNode() {
    abstract val value: String
    abstract val isString: Boolean
    abstract val isBoolean: Boolean
    abstract val isNumber: Boolean
}

data class JsonLiteral internal constructor(
    override val value: String,
    override val isString: Boolean = false,
    override val isBoolean: Boolean = false,
    override val isNumber: Boolean = false
) : PrimitiveNode() {

    constructor(string: String) : this(string, isString = true)
    constructor(number: Number) : this(number.toString(), isNumber = true)
    constructor(boolean: Boolean) : this(boolean.toString(), isBoolean = true)

    override fun toString(): String {
        return if (isString) JsonString.escape(value).toQuotedString() else value
    }
}

object JsonNull : PrimitiveNode() {
    override val value: String = "null"
    override val isBoolean: Boolean = false
    override val isString: Boolean = false
    override val isNumber: Boolean = false

    override fun toString() = value
}
