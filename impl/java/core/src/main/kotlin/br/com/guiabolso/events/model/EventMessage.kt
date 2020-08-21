package br.com.guiabolso.events.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class EventMessage(
    val code: String,
    val parameters: JsonObject
)
