package br.com.guiabolso.events.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
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
