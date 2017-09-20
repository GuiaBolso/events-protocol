package br.com.guiabolso.events.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject


interface Event {
    val name: String
    val version: Int
    val id: String
    val flowId: String
    val payload: JsonElement
    val identity: JsonObject
    val auth: JsonObject
    val metadata: JsonObject
}

data class RequestEvent(
        override val name: String,
        override val version: Int,
        override val id: String,
        override val flowId: String,
        override val payload: JsonElement,
        override val identity: JsonObject,
        override val auth: JsonObject,
        override val metadata: JsonObject
) : Event

data class ResponseEvent(
        override val name: String,
        override val version: Int,
        override val id: String,
        override val flowId: String,
        override val payload: JsonElement,
        override val identity: JsonObject,
        override val auth: JsonObject,
        override val metadata: JsonObject
) : Event