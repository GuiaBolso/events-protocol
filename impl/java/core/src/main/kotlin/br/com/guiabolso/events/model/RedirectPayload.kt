package br.com.guiabolso.events.model

import com.google.gson.annotations.SerializedName

data class RedirectPayload(
    @SerializedName("url")
    val url: String,
    @SerializedName("queryParameters")
    val queryParameters: Map<String, Any> = emptyMap()
)
