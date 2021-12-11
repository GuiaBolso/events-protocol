package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.JsonParser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MoshiParser(builder: Moshi.Builder.() -> Moshi.Builder = { this }) : JsonParser {
    // TODO REVISAR ORDEM
    private val moshi =
        Moshi.Builder()
            .run(builder)
            .add(JsonNodeFactory)
            .addLast(KotlinJsonAdapterFactory())
            .build()

    override fun toJsonTree(any: Any?): JsonNode {
        TODO("Not yet implemented")
    }

    override fun <T> fromJson(json: String, clazz: Class<T>): T {
        return moshi.adapter(clazz).fromJson(json) ?: throw IllegalStateException()
    }

    override fun <T> JsonNode.convertTo(clazz: Class<T>): T {
        return moshi.adapter(clazz).fromJsonValue(this) ?: error("")
    }
}

data class RequestString(val string: String)

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val json = """
    {
        "string": "String"
    }
""".trimIndent()

    val parser = MoshiParser()
    val node = parser.fromJson(json, JsonNode::class.java)
    println(node)
    println(with(parser) {
        node.convertTo<RequestString>()
    })
}
