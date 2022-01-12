package br.com.guiabolso.events.json.moshi

import br.com.guiabolso.events.json.JsonNode

data class DataWrapper(val data: JsonNode)

data class Sample(
    val list: List<Any>,
    val string: String,
    val int: Int,
    val boolean: Boolean,
    val map: Map<String, Any>,
    val any: Any?
)
