package br.com.guiabolso.tracing.aspect

import java.io.Closeable

object StatsDTags : Closeable {

    private val tags = InheritableThreadLocal<MutableMap<String, String>>()

    fun addTag(key: String, value: String) {
        if (tags.get() == null) tags.set(mutableMapOf())
        tags.get()[key] = value
    }

    fun getTags(): Map<String, String> =
        tags.get()?.let { map ->
            map.filterNot { entry -> entry.key == "prefix" }
        } ?: emptyMap()

    fun get(key: String): String? = tags.get()[key]

    override fun close() {
        if (this.get("prefix") == "") tags.remove()
    }

}