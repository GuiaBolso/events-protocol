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
            .addLast(KotlinJsonAdapterFactory())
            .apply(builder)
            .build()

    override fun toJson(any: Any?): String {
        return nonNullAdapterFor(Any::class.java).execute { toJson(any) }
    }

    override fun toJsonTree(any: Any?): JsonNode {
        return when (any) {
            null -> JsonNull
            is JsonNode -> any
            else -> {
                val adapter = moshi.adapter(Any::class.java).serializeNulls()
                val jsonValue = adapter.execute { toJsonValue(any) }
                nonNullAdapterFor(JsonNode::class.java).execute { fromJsonValue(jsonValue)!! }
            }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return nonNullAdapterFor(clazz).execute { fromJson(json)!! }
    }

    override fun <T> fromJson(json: String, type: Type): T {
        val adapter = nonNullAdapterFor<T>(type)
        return adapter.execute { fromJson(json)!! }
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        val jsonValue = jsonNodeAdapter().execute { toJsonValue(jsonNode) }

        return nonNullAdapterFor<T>(type).execute { fromJsonValue(jsonValue)!! }
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        val jsonValue = jsonNodeAdapter().execute { toJsonValue(jsonNode) }

        return nonNullAdapterFor(clazz).execute { fromJsonValue(jsonValue)!! }
    }

    private fun <T, R> MoshiJsonAdapter<T>.execute(block: MoshiJsonAdapter<T>.() -> R): R {
        return try {
            this.block()
        } catch (e: com.squareup.moshi.JsonDataException) {
            throw JsonDataException(message = e.message, cause = e)
        }
    }

    private fun jsonNodeAdapter() = moshi.adapter(JsonNode::class.java)

    private fun <T> nonNullAdapterFor(type: Type) = moshi.adapter<T>(type).nonNull()

    private fun <T> nonNullAdapterFor(clazz: Class<T>) = moshi.adapter(clazz).nonNull()
}
