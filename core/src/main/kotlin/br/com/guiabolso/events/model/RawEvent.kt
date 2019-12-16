package br.com.guiabolso.events.model

import com.google.gson.JsonElement

data class RawEvent(
    val name: String?,
    val version: Int?,
    val id: String?,
    val flowId: String?,
    val payload: JsonElement?,
    val identity: JsonElement?,
    val auth: JsonElement?,
    val metadata: JsonElement?
)