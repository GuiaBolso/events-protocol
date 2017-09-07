package br.com.guiabolso.events.builder

import br.com.guiabolso.events.model.Event
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

object EventBuilder {

    private val mapper = Gson()

    @JvmStatic
    fun response(event: Event, payload: JsonElement, identity: JsonObject = JsonObject(), auth: JsonObject = JsonObject(), metadata: JsonObject = JsonObject()) = Event(
            name = "${event.name}:response",
            version = event.version,
            id = event.id,
            flowId = event.flowId,
            payload = payload,
            identity = identity,
            auth = auth,
            metadata = metadata
    )

    @JvmStatic
    fun notFound(event: Event, messageCode: String, parameters: Map<String, Any?>, identity: JsonObject = JsonObject(), auth: JsonObject = JsonObject(), metadata: JsonObject = JsonObject()): Event {
        return error(event, "notFound", messageCode, parameters, identity, auth, metadata)
    }

    @JvmStatic
    fun error(event: Event, errorType: String, messageCode: String, parameters: Map<String, Any?>, identity: JsonObject = JsonObject(), auth: JsonObject = JsonObject(), metadata: JsonObject = JsonObject()) = Event(
            name = "${event.name}:$errorType",
            version = event.version,
            id = event.id,
            flowId = event.flowId,
            payload = JsonObject().apply {
                addProperty("code", messageCode)
                add("parameters", mapper.toJsonTree(parameters))
            },
            identity = identity,
            auth = auth,
            metadata = metadata
    )

    @JvmStatic
    fun badProtocol(messageCode: String, parameters: Map<String, Any?>) = Event(
            name = "badProtocol",
            version = 1,
            id = UUID.randomUUID().toString(),
            flowId = UUID.randomUUID().toString(),
            payload = JsonObject().apply {
                addProperty("code", messageCode)
                add("parameters", mapper.toJsonTree(parameters))
            },
            identity = JsonObject(),
            auth = JsonObject(),
            metadata = JsonObject()
    )

}

