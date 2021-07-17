package br.com.guiabolso.events.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class RawEvent(
    @SerializedName("name")
    val name: String?,
    @SerializedName("version")
    val version: Int?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("flowId")
    val flowId: String?,
    @SerializedName("payload")
    val payload: JsonElement?,
    @SerializedName("identity")
    val identity: JsonObject?,
    @SerializedName("auth")
    val auth: JsonObject?,
    @SerializedName("metadata")
    val metadata: JsonObject?
)
