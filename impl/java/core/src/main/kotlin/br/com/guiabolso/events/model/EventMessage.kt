package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonNode

data class EventMessage(
    val code: String,
    val parameters: Map<String, JsonNode?>
)
