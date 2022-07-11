package br.com.guiabolso.events.json

import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

inline fun <reified T> JsonAdapter.fromJsonOrNull(json: String): T? {
    return try {
        fromJson<T>(json, typeOf<T>().javaType)
    } catch (ignore: JsonDataException) {
        null
    }
}

inline fun <reified T> JsonAdapter.fromJsonOrNull(jsonNode: JsonNode): T? {
    return try {
        fromJson<T>(jsonNode, typeOf<T>().javaType)
    } catch (ignore: JsonDataException) {
        null
    }
}

inline fun <reified T> JsonAdapter.fromJson(json: String): T {
    return this.fromJson(json, typeOf<T>().javaType)
}

inline fun <reified T> JsonAdapter.fromJson(node: JsonNode): T {
    return this.fromJson(node, typeOf<T>().javaType)
}
