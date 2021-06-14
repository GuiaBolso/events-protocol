package br.com.guiabolso.events.model

import com.google.gson.annotations.SerializedName

data class EventMessage(
    @SerializedName("code")
    val code: String,
    @SerializedName("parameters")
    val parameters: Map<String, Any?>
)
