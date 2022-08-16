package br.com.guiabolso.events.json.gson

import br.com.guiabolso.events.json.JsonNode
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.gson.adapters.EventTypeAdapterFactory
import br.com.guiabolso.events.json.gson.adapters.JsonNodeAdapter
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import java.util.UUID

val gson = GsonBuilder()
    .registerTypeHierarchyAdapter(JsonNode::class.java, JsonNodeAdapter)
    .registerTypeAdapterFactory(EventTypeAdapterFactory)
    .serializeNulls()
    .create()!!

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

fun String.toJsonReader() = JsonReader(StringReader(this).buffered())

fun jsonWriter(out: Writer = StringWriter()) = JsonWriter(out)
