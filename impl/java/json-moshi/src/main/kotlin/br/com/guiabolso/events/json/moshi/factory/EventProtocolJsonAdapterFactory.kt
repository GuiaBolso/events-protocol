package br.com.guiabolso.events.json.moshi.factory

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.moshi.adapter.EventProtocolAdapter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.rawType
import java.lang.reflect.Type

object EventProtocolJsonAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<out Event>? {
        val rawType = type.rawType
        if (!Event::class.java.isAssignableFrom(rawType)) return null

        val jsonNodeAdapter = moshi.adapter(JsonNode::class.java)
        return when {
            RequestEvent::class.java.isAssignableFrom(rawType) -> {
                EventProtocolAdapter<RequestEvent>(
                    delegate = moshi.nextAdapter(this, type, annotations),
                    jsonNodeAdapter = jsonNodeAdapter
                )
            }
            ResponseEvent::class.java.isAssignableFrom(rawType) ->
                EventProtocolAdapter<ResponseEvent>(
                    delegate = moshi.nextAdapter(this, type, annotations),
                    jsonNodeAdapter = jsonNodeAdapter
                )
            else -> error("Unmapped event protocol class ${rawType.name}")
        }
    }
}
