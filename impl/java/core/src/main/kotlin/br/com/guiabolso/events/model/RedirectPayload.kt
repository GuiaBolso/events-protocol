package br.com.guiabolso.events.model

import br.com.guiabolso.events.json.JsonNode

data class RedirectPayload(
    val url: String,
    val queryParameters: Map<String, JsonNode> = emptyMap()
)
