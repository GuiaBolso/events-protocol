package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonAdapterProducer
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.handler.toContext

fun requestEventContext(
    jsonAdapter: JsonAdapter = JsonAdapterProducer.mapper,
    requestEvent: RequestEvent = requestEvent(jsonAdapter = jsonAdapter),
) = requestEvent.toContext(jsonAdapter)

fun requestEvent(
    payload: Any? = PrimitiveNode(42),
    jsonAdapter: JsonAdapter = JsonAdapterProducer.mapper,
) = buildRequestEvent().copy(
    payload = jsonAdapter.toJsonTree(payload)
)

fun RequestEvent.context(jsonAdapter: JsonAdapter = JsonAdapterProducer.mapper) = this.toContext(jsonAdapter)
