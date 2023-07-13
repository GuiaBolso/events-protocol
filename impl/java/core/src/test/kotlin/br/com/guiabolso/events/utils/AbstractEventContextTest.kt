package br.com.guiabolso.events.utils

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.JsonAdapter
import br.com.guiabolso.events.json.JsonAdapterProducer
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.toPrimitiveNode
import br.com.guiabolso.events.model.AbstractEventContext
import br.com.guiabolso.events.model.RequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AbstractEventContextTest : StringSpec({

    "should delegate all event property access to event argument" {
        val requestEvent = EventBuilderForTest.buildRequestEvent().copy(
            identity = TreeNode(
                "user" to TreeNode(
                    "id" to 1.toPrimitiveNode(),
                    "type" to "CUSTOMER".toPrimitiveNode()
                )
            )
        )
        val context = TestEventContext(requestEvent, JsonAdapterProducer.mapper)

        context.name shouldBe requestEvent.name
        context.version shouldBe requestEvent.version
        context.id shouldBe requestEvent.id
        context.flowId shouldBe requestEvent.flowId
        context.payload shouldBe requestEvent.payload
        context.identity shouldBe requestEvent.identity
        context.auth shouldBe requestEvent.auth
        context.metadata shouldBe requestEvent.metadata

        context.user shouldBe requestEvent.user
        context.userId shouldBe requestEvent.userId
        context.userIdAsString shouldBe requestEvent.userIdAsString
        context.origin shouldBe requestEvent.origin
    }

    "should delegate to event function when converting event data" {
        val data = TreeNode("a" to PrimitiveNode("Test"), "b" to PrimitiveNode(1))
        val requestEvent = EventBuilderForTest.buildRequestEvent().copy(
            payload = data,
            metadata = data,
            auth = data,
            identity = data
        )

        val jsonAdapter = JsonAdapterProducer.mapper
        val context = TestEventContext(requestEvent, jsonAdapter)

        context.payloadAs<Data>() shouldBe requestEvent.payloadAs(jsonAdapter)
        context.identityAs<Data>() shouldBe requestEvent.payloadAs(jsonAdapter)
        context.authAs<Data>() shouldBe requestEvent.payloadAs(jsonAdapter)
    }
})

private data class Data(val a: String, val b: Int)

private class TestEventContext(
    override val event: RequestEvent,
    override val jsonAdapter: JsonAdapter,
) : AbstractEventContext<RequestEvent>()
