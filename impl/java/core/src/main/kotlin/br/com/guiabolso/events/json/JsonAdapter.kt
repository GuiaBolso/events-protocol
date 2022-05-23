package br.com.guiabolso.events.json

import java.lang.reflect.Type

interface JsonAdapter {

    fun toJson(any: Any?): String

    fun toJsonTree(any: Any?): JsonNode

    fun <T> toJson(any: T?, type: Type): String

    fun <T> fromJson(json: String, clazz: Class<T>): T

    fun <T> fromJson(json: String, type: Type): T

    fun <T> fromJson(jsonNode: JsonNode, type: Type): T

    fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T
}
