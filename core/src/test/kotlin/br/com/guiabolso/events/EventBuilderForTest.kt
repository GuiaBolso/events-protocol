package br.com.guiabolso.events

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

object EventBuilderForTest {

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

}