package br.com.guiabolso.events.validation

import com.google.gson.JsonNull
import com.google.gson.JsonObject

fun <T> JsonObject.withCheckedJsonNull(checkedParam: String, block: (jsonObject: JsonObject) -> T?): T? =
        if (this.get(checkedParam) is JsonNull) {
            null
        } else {
            block(this)
        }