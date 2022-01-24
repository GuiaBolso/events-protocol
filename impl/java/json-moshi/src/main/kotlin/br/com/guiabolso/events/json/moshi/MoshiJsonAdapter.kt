package br.com.guiabolso.events.json.moshi

import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonNull
import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.factory.JsonNodeFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type

class MoshiJsonAdapter(builder: Moshi.Builder.() -> Moshi.Builder = { this }) : JsonAdapter {
    private val moshi =
        Moshi.Builder()
            .add(JsonNodeFactory)
            .add(EventProtocolJsonAdapterFactory)
            .addLast(KotlinJsonAdapterFactory())
            .run(builder)
            .build()

    override fun toJson(any: Any?): String {
        return safeAdapterFor(Any::class.java).toJson(any)
    }

    override fun toJsonTree(any: Any?): JsonNode {
        return when (any) {
            null -> JsonNull
            is JsonNode -> any
            else -> {
                val adapter = moshi.adapter(Any::class.java).serializeNulls()
                val jsonValue = adapter.toJsonValue(any)
                safeAdapterFor(JsonNode::class.java).fromJsonValue(jsonValue)!!
            }
        }
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return safeAdapterFor(clazz).fromJson(json)!!
    }

    override fun <T> fromJson(json: String, type: Type): T {
        val adapter = nonNullAdapterFor<T>(type)
        return adapter.fromJson(json)!!
    }

    override fun <T> fromJson(jsonNode: JsonNode, type: Type): T {
        val jsonValue = jsonNodeAdapter().toJsonValue(jsonNode)
        return nonNullAdapterFor<T>(type).fromJsonValue(jsonValue)!!
    }

    override fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T {
        val jsonValue = jsonNodeAdapter().toJsonValue(jsonNode)
        return safeAdapterFor(clazz).fromJsonValue(jsonValue)!!
    }

    private fun jsonNodeAdapter() = moshi.adapter(JsonNode::class.java)

    private fun <T> nonNullAdapterFor(type: Type) = moshi.adapter<T>(type).nonNull()

    private fun <T> safeAdapterFor(clazz: Class<T>) = moshi.adapter(clazz).nonNull()
}
