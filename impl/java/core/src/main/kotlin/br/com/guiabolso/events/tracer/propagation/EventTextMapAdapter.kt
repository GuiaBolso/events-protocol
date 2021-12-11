package br.com.guiabolso.events.tracer.propagation

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.Event
import io.opentracing.propagation.TextMap
import kotlin.collections.MutableMap.MutableEntry
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

class EventTextMapAdapter(private val event: Event) : TextMap {

    override fun iterator(): MutableIterator<MutableEntry<String, String>> {
        val traceElement = event.metadata["trace"]?.run {
            MapperHolder.mapper.fromJson<MutableMap<String, String>>(
                jsonNode = this,
                type = typeOf<MutableMap<String, String>>().javaType
            )
        }
        return (traceElement ?: mutableMapOf()).iterator()
    }

    override fun put(key: String, value: String?) {
        traceElement()[key] = MapperHolder.mapper.toJsonTree(value)
    }

    private fun traceElement(): JsonNode.TreeNode {
        val metadata = event.metadata
        if (!metadata.contains("trace")) {
            metadata["trace"] = JsonNode.TreeNode()
        }
        return metadata["trace"] as JsonNode.TreeNode
    }
}
