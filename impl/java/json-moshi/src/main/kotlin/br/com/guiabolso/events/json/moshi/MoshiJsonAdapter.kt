package br.com.guiabolso.events.json.moshi

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonDataException
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.factory.JsonNodeFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import com.squareup.moshi.JsonAdapter as MoshiJsonAdapter

class MoshiJsonAdapter(builder: Moshi.Builder.() -> Unit = {}) : JsonAdapter {
    private val moshi =
        Moshi.Builder()
            .add(JsonNodeFactory)
            .add(EventProtocolJsonAdapterFactory)
            .apply(builder)
            .addLast(KotlinJsonAdapterFactory())
            .build()

    override fun <T> toJson(any: T?, type: Type): String {
        return moshi.adapter<T>(type).nullSafe().toJson(any)
    }

    override fun toJson(any: Any?): String {
        return if (any == null) "null"
        else moshi.adapter<Any>(jsonTypeOf(any)).nullSafe().execute { this.toJson(any) }
    }

    private fun jsonTypeOf(any: Any): Class<out Any> {
        val javaClass = any.javaClass
        return when {
            JsonNode::class.java.isAssignableFrom(javaClass) -> javaClass
            Map::class.java.isAssignableFrom(javaClass) -> Map::class.java
            Collection::class.java.isAssignableFrom(javaClass) -> Collection::class.java
            else -> javaClass
        }
    }

    override fun toJsonTree(any: Any?): JsonNode {
        return when (any) {
            null -> JsonNull
            is JsonNode -> any
            else -> {
                val adapter = moshi.adapter<Any>(jsonTypeOf(any))
                val jsonValue = adapter.execute { toJsonValue(any) }
                moshi.adapter(JsonNode::class.java).nonNull().execute { fromJsonValue(jsonValue)!! }
            }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return moshi.adapter(clazz).nonNull().execute { fromJson(json)!! }
    }

    override fun <T> fromJson(json: String, type: Type): T {
        val adapter = moshi.adapter<T>(type).nonNull()
        return adapter.execute { fromJson(json)!! }
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        val jsonValue = jsonNodeAdapter().execute { toJsonValue(jsonNode) }

        return moshi.adapter<T>(type).nonNull().execute { fromJsonValue(jsonValue)!! }
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        val jsonValue = jsonNodeAdapter().execute { toJsonValue(jsonNode) }

        return moshi.adapter(clazz).nonNull().execute { fromJsonValue(jsonValue)!! }
    }

    private fun <T, R> MoshiJsonAdapter<T>.execute(block: MoshiJsonAdapter<T>.() -> R): R {
        return try {
            this.block()
        } catch (e: com.squareup.moshi.JsonDataException) {
            throw JsonDataException(message = e.message, cause = e)
        }
    }

    private fun jsonNodeAdapter() = moshi.adapter(JsonNode::class.java)
}
