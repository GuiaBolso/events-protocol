package br.com.guiabolso.events.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject


sealed class Event {
    abstract val name: String
    abstract val version: Int
    abstract val id: String
    abstract val flowId: String
    abstract val payload: JsonElement
    abstract val identity: JsonObject
    abstract val auth: JsonObject
    abstract val metadata: JsonObject
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
) : Event()

data class ResponseEvent(
        override val name: String,
        override val version: Int,
        override val id: String,
        override val flowId: String,
        override val payload: JsonElement,
        override val identity: JsonObject,
        override val auth: JsonObject,
        override val metadata: JsonObject
) : Event()