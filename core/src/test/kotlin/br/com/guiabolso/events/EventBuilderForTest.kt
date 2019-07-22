package br.com.guiabolso.events

import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

object EventBuilderForTest {

    fun buildRawRequestEvent() = RawEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = JsonObject(),
        auth = JsonObject(),
        metadata = JsonObject()
    )

    fun buildRequestEvent() = RequestEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = JsonObject(),
        auth = JsonObject(),
        metadata = JsonObject()
    )

    fun buildResponseEvent() = ResponseEvent(
        name = "event:name:response",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = JsonObject(),
        auth = JsonObject(),
        metadata = JsonObject()
    )

    fun buildRedirectEvent() = ResponseEvent(
        name = "event:name:redirect",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonObject().apply {
            addProperty("url", "https://www.google.com")
            add("queryParameters", JsonObject())
        },
        identity = JsonObject(),
        auth = JsonObject(),
        metadata = JsonObject()
    )

    fun buildRequestEventString(event: RequestEvent = buildRequestEvent()) =
        MapperHolder.mapper.toJson(event)!!

}