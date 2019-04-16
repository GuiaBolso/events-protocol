package br.com.guiabolso.tracing.aspect

import java.io.Closeable
object StatsDTags : Closeable {

    private val tags = InheritableThreadLocal<MutableMap<String, String>>()

    @JvmStatic
    fun addTag(key: String, value: String) {
        if (tags.get() == null) tags.set(mutableMapOf())
        tags.get()[key] = value
    }

    @JvmStatic
    fun getTags(): Map<String, String> =
            tags.get()?.let { map ->
                map.filterNot { entry -> entry.key == "prefix" }
            } ?: emptyMap()

    @JvmStatic
    fun get(key: String): String? = tags.get()?.get(key)

    override fun close() {
        if (this.get("prefix") == "") tags.remove()
    }

}

