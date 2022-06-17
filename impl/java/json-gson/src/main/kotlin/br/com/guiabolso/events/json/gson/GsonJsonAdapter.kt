package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.gson.adapters.EventTypeAdapterFactory
import br.com.guiabolso.events.json.gson.adapters.JsonNodeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

class GsonJsonAdapter(configure: GsonBuilder.() -> Unit = { serializeNulls() }) : JsonAdapter {
    private val gson =
        GsonBuilder()
            .apply(configure)
            .registerTypeHierarchyAdapter(JsonNode::class.java, JsonNodeAdapter)
            .registerTypeAdapterFactory(EventTypeAdapterFactory)
            .create()

    override fun <T> toJson(any: T?): String {
        return gson.toJson(any)!!
    }

    override fun <T> toJson(any: T?, type: Type): String {
        return gson.toJson(any, type)!!
    }

    override fun toJsonTree(any: Any?): JsonNode {
        return when (any) {
            null -> JsonNull
            is JsonNode -> any
            else -> {
                val jsonElement = gson.toJsonTree(any)
                gson.execute { fromJson(jsonElement, JsonNode::class.java) }
            }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return gson.execute { fromJson(json, clazz) }
    }

    override fun <T> fromJson(json: String, type: Type): T {
        return gson.execute { fromJson<T>(json, type) }
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        val jsonElement = gson.toJsonTree(jsonNode)
        return gson.execute { fromJson<T>(jsonElement, type) }
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        val jsonElement = gson.toJsonTree(jsonNode)
        return gson.execute { fromJson(jsonElement, clazz) }
    }

    private fun <T> Gson.execute(block: Gson.() -> T): T {
        val result = try {
            this.block()
        } catch (e: RuntimeException) {
            throw JsonDataException(message = e.message, cause = e)
        }

        return result ?: throw JsonDataException(message = "Unexpected null as a result from deserialization")
    }
}
