package br.com.guiabolso.events.json.moshi

import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.factory.JsonNodeFactory
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.UUID

val moshi: Moshi =
    Moshi.Builder()
        .addLast(JsonNodeFactory)
        .addLast(EventProtocolJsonAdapterFactory)
        .addLast(KotlinJsonAdapterFactory())
        .build()

val Event.json
    get() =
        """{"name":"$name","version":$version,"id":"$id","flowId":"$flowId","payload":$payload,""" +
                """"identity":$identity,"auth":$auth,"metadata":$metadata}"""

fun createRequestEvent() = RequestEvent(
    name = "event:name",
    version = 1,
    id = "id",
    flowId = "flowId",
    identity = TreeNode("userId" to PrimitiveNode(42)),
    metadata = TreeNode("origin" to PrimitiveNode("test")),
    auth = TreeNode("token" to PrimitiveNode(UUID.randomUUID().toString())),
    payload = TreeNode("key" to PrimitiveNode("value"))
)

fun createResponseEvent() = with(createRequestEvent()) {
    ResponseEvent(
        id = id,
        name = "$name:response",
        version = version,
        flowId = flowId,
        payload = payload,
        identity = identity,
        auth = auth,
        metadata = metadata,
    )
}
