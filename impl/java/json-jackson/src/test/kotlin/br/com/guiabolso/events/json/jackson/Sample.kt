package br.com.guiabolso.events.json.jackson

data class Sample(
    val list: List<Any>,
    val string: String,
    val int: Int,
    val boolean: Boolean,
    val map: Map<String, Any>,
    val any: Any?
)
