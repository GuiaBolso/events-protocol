package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

object EventTypeAdapterFactory : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (!Event::class.java.isAssignableFrom(rawType)) return null

        val jsonNodeAdapter = gson.getAdapter(JsonNode::class.java)
        return when {
            RequestEvent::class.java.isAssignableFrom(rawType) -> {
                EventTypeAdapter(
                    jsonNodeAdapter = jsonNodeAdapter,
                    readerDelegate = gson.getDelegateAdapter(this, TypeToken.get(RequestEvent::class.java))
                ) as TypeAdapter<T>
            }

            ResponseEvent::class.java.isAssignableFrom(rawType) -> {
                EventTypeAdapter(
                    jsonNodeAdapter = jsonNodeAdapter,
                    readerDelegate = gson.getDelegateAdapter(this, TypeToken.get(ResponseEvent::class.java))
                ) as TypeAdapter<T>
            }
            else -> error("Unmapped event protocol class ${rawType.name}")
        }
    }
}
