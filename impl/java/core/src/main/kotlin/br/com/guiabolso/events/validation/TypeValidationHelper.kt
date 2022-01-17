package br.com.guiabolso.events.validation

import com.google.gson.JsonNull
import com.google.gson.JsonObject

fun <T> JsonObject.withCheckedJsonNull(checkedParam: String, block: (jsonObject: JsonObject) -> T?): T? =
    if (this.get(checkedParam) is JsonNull) {
        null
    } else {
        block(this)
    }

fun JsonObject.string(key: String): String? {
    val element = this.get(key)
    return if (element != null && element !is JsonNull && element.isJsonPrimitive) {
        element.asString
    } else null
}

fun JsonObject.long(key: String): Long? {
    val element = this.get(key)
    return if (element != null && element !is JsonNull && element.isJsonPrimitive) {
        try {
            element.asLong
        } catch (e: NumberFormatException) {
            null
        }
    } else null
}

fun JsonObject.jsonObject(key: String): JsonObject? {
    val element = this.get(key)
    return if (element != null && element !is JsonNull && element.isJsonObject) {
        element.asJsonObject
    } else null
}
