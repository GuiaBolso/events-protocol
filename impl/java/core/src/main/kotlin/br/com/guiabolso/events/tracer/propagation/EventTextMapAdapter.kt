package br.com.guiabolso.events.tracer.propagation

import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.Event
import io.opentracing.propagation.TextMap
import kotlin.collections.MutableMap.MutableEntry
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

class EventTextMapAdapter(private val event: Event) : TextMap {

    override fun iterator(): MutableIterator<MutableEntry<String, String>> {
        val traceElement = event.metadata["trace"]?.run {
            mapper.fromJson<MutableMap<String, String>>(
                jsonNode = this,
                type = typeOf<MutableMap<String, String>>().javaType
            )
        }
        return (traceElement ?: mutableMapOf()).iterator()
    }

    override fun put(key: String, value: String?) {
        traceElement()[key] = mapper.toJsonTree(value)
    }

    private fun traceElement(): TreeNode {
        val metadata = event.metadata
        if (!metadata.containsKey("trace")) {
            metadata["trace"] = TreeNode()
        }
        return metadata["trace"] as TreeNode
    }
}
