package br.com.guiabolso.events

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object EventBuilderForTest {

    fun buildRawRequestEvent() = RawEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = buildJsonObject {},
        auth = buildJsonObject {},
        metadata = buildJsonObject {}
    )

    fun buildRequestEvent() = RequestEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = buildJsonObject {},
        auth = buildJsonObject {},
        metadata = buildJsonObject {}
    )

    fun buildResponseEvent() = ResponseEvent(
        name = "event:name:response",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonPrimitive(42),
        identity = buildJsonObject {},
        auth = buildJsonObject {},
        metadata = buildJsonObject {}
    )

    fun buildRedirectEvent() = ResponseEvent(
        name = "event:name:redirect",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = buildJsonObject {
            put("url", "https://www.google.com")
            putJsonObject("queryParameters") {}
        },
        identity = buildJsonObject {},
        auth = buildJsonObject {},
        metadata = buildJsonObject {}
    )
}
