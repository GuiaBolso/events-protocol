package br.com.guiabolso.events

import br.com.guiabolso.events.json.JsonLiteral
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

object EventBuilderForTest {

    fun buildRawRequestEvent() = RawEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonLiteral(42),
        identity = TreeNode(),
        auth = TreeNode(),
        metadata = TreeNode()
    )

    fun buildRequestEvent() = RequestEvent(
        name = "event:name",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonLiteral(42),
        identity = TreeNode(),
        auth = TreeNode(),
        metadata = TreeNode()
    )

    fun buildResponseEvent() = ResponseEvent(
        name = "event:name:response",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = JsonLiteral("42"),
        identity = TreeNode(),
        auth = TreeNode(),
        metadata = TreeNode()
    )

    fun buildRedirectEvent() = ResponseEvent(
        name = "event:name:redirect",
        version = 1,
        id = "id",
        flowId = "flowId",
        payload = TreeNode().apply {
            this["url"] = JsonLiteral("https://www.google.com")
            this["queryParameters"] = TreeNode()
        },
        identity = TreeNode(),
        auth = TreeNode(),
        metadata = TreeNode()
    )
}
