package br.com.guiabolso.events.tracer.propagation

import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.Event
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.opentracing.propagation.TextMap
import kotlin.collections.MutableMap.MutableEntry

class EventTextMapAdapter(private val event: Event) : TextMap {

    override fun iterator(): MutableIterator<MutableEntry<String, String>> {
        val traceElement: JsonElement? = event.metadata["trace"]
        val traceMap: MutableMap<String, String> = mapper.fromJson(traceElement) ?: mutableMapOf()

        return traceMap.iterator()
    }

    override fun put(key: String, value: String?) {
        traceElement().addProperty(key, value)
    }

    private fun traceElement(): JsonObject {
        val metadata = event.metadata
        if (!metadata.has("trace")) {
            metadata.add("trace", JsonObject())
        }
        return metadata["trace"].asJsonObject
    }

    private inline fun <reified T> Gson.fromJson(element: JsonElement?): T? = fromJson(element, T::class.java)
}
