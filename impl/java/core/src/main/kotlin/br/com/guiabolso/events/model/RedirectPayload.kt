package br.com.guiabolso.events.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class RedirectPayload(
    val url: String,
    val queryParameters: Map<String, @Contextual Any> = emptyMap()
)
