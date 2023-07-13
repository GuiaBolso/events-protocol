package br.com.guiabolso.events.tracer.propagation

import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.stringOrNull
import br.com.guiabolso.events.json.toPrimitiveNode
import br.com.guiabolso.events.json.treeNode
import br.com.guiabolso.events.model.Event
import io.opentracing.propagation.TextMap
import kotlin.collections.MutableMap.MutableEntry

class EventTextMapAdapter(private val event: Event) : TextMap {

    override fun iterator(): MutableIterator<MutableEntry<String, String>> {
        val traceElement = event.metadata["trace"]?.run {
            this.treeNode
                .entries
                .associateTo(mutableMapOf()) { (k, v) -> k to v.stringOrNull.toString() }
        }

        return (traceElement ?: mutableMapOf()).iterator()
    }

    override fun put(key: String, value: String?) {
        traceElement()[key] = value.toPrimitiveNode()
    }

    private fun traceElement(): TreeNode {
        val metadata = event.metadata
        if (!metadata.containsKey("trace")) {
            metadata["trace"] = TreeNode()
        }
        return metadata["trace"] as TreeNode
    }
}
