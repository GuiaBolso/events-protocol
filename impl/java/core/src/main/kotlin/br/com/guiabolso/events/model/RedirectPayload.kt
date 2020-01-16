package br.com.guiabolso.events.model

data class RedirectPayload(
    val url: String,
    val queryParameters: Map<String, Any> = emptyMap()
)