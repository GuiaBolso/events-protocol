package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonParser
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class GsonParser(builder: GsonBuilder.() -> Unit = {}) : JsonParser {

    private val gson = GsonBuilder()
        .registerTypeHierarchyAdapter(JsonNode::class.java, JsonNodeTypeAdapter)
        .serializeNulls()
        .apply(builder)
        .create()

    override fun toJsonTree(any: Any?): JsonNode {
        val jsonElement = gson.toJsonTree(any)
        return gson.fromJson(jsonElement, JsonNode::class.java)
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return gson.fromJson<T>(json, clazz)
    }

    override fun <T> JsonNode.convertTo(clazz: Class<T>): T {
        val jsonElement = gson.toJsonTree(this)
        return gson.fromJson(jsonElement, clazz)
    }

    override fun <T> JsonNode.convertTo(): T {
        val jsonElement = gson.toJsonTree(this)
        return gson.fromJson(jsonElement, object : TypeToken<T>() {}.type)
    }

}

data class Request(val node: JsonNode)

fun main() {
    val json = """
        {
            "node": {
                "node": 1,
                "nodes": [1,2,3]
            }
        }
    """.trimIndent()


    val parser = GsonBuilder().registerTypeHierarchyAdapter(JsonNode::class.java, JsonNodeTypeAdapter).create()
    val reques = parser.fromJson<Request>(json, object : TypeToken<Request>() {}.type)
    val reques2 = GsonParser().fromJson<Request>(json, Request::class.java)
    println(reques2)
    println(reques)
}
