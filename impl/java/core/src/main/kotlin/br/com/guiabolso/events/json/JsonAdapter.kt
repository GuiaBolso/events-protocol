package br.com.guiabolso.events.json

import java.io.InputStream
import java.lang.reflect.Type

interface JsonAdapter {

    fun <T> toJson(any: T?): String

    fun <T> toJsonTree(any: T?): JsonNode

    fun <T> toJson(any: T?, type: Type): String

    fun <T> fromJson(json: String, clazz: Class<T>): T

    fun <T> fromJson(json: String, type: Type): T

    fun <T> fromJson(json: InputStream, type: Type): T

    fun <T> fromJson(jsonNode: JsonNode, type: Type): T

    fun <T> fromJson(jsonNode: JsonNode, clazz: Class<T>): T
}
