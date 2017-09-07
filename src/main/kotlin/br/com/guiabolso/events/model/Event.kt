package br.com.guiabolso.events.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class Event(
        val name: String,
        val version: Int,
        val id: String,
        val flowId: String,
        val payload: JsonElement,
        val identity: JsonObject,
        val auth: JsonObject,
        val metadata: JsonObject
)