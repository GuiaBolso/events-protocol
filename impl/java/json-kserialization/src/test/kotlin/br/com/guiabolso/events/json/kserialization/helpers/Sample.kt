package br.com.guiabolso.events.json.kserialization.helpers

import kotlinx.serialization.Serializable

@Serializable
data class Sample(
    val list: List<Double>,
    val string: String,
    val int: Int,
    val boolean: Boolean,
    val map: Map<String, String>,
    val any: Other?
)

@Serializable
data class Other(val value: String)
