package br.com.guiabolso.events.test

import br.com.guiabolso.events.builder.EventBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class GeneralEventsMatchersTests : FunSpec({

    val event = EventBuilder.event {
        name = "a:b"
        version = 3
        id = "id"
        flowId = "flowId"
        payload(complexMap)
        identity(complexMap)
        auth(complexMap)
        metadata(complexMap)
    }

    test("should have name") {
        event shouldHaveName "a:b"
        event shouldNotHaveName "poop"

        shouldThrow<AssertionError> { event shouldHaveName "x" }
        shouldThrow<AssertionError> { event shouldNotHaveName "a:b" }
    }

    test("Should have version") {
        event shouldHaveVersion 3
        event shouldNotHaveVersion 4

        shouldThrow<AssertionError> { event shouldHaveVersion 4 }
        shouldThrow<AssertionError> { event shouldNotHaveVersion 3 }
    }

    test("Should have id") {
        event shouldHaveId "id"
        event shouldNotHaveId "poop"

        shouldThrow<AssertionError> { event shouldHaveId "poop" }
        shouldThrow<AssertionError> { event shouldNotHaveId "id" }
    }

    test("Should have FlowId") {
        event shouldHaveFlowId "flowId"
        event shouldNotHaveFlowId "poop"

        shouldThrow<AssertionError> { event shouldHaveFlowId "poop" }
        shouldThrow<AssertionError> { event shouldNotHaveFlowId "flowId" }
    }

    test("Should have payload") {
        event.shouldContainPayload("a", "b")
        event.shouldNotContainPayload("a", "c")
        shouldThrow<AssertionError> { event.shouldNotContainPayload("a", "b") }
        shouldThrow<AssertionError> { event.shouldContainPayload("a", "c") }

        event.shouldContainPayload("c.a", "b")
        event.shouldNotContainPayload("c.a", "d")
        shouldThrow<AssertionError> { event.shouldNotContainPayload("c.a", "b") }
        shouldThrow<AssertionError> { event.shouldContainPayload("c.a", "d") }

        event shouldHavePayload complexMap
        event shouldNotHavePayload buildJsonObject { put("a", "b") }
        event shouldNotHavePayload buildJsonObject { }

        shouldThrow<AssertionError> { event shouldNotHavePayload complexMap }
        shouldThrow<AssertionError> { event shouldHavePayload buildJsonObject { put("a", "b") } }
        shouldThrow<AssertionError> { event shouldHavePayload buildJsonObject { } }
    }

    test("Should have identity") {
        event.shouldContainIdentity("a", "b")
        event.shouldNotContainIdentity("a", "c")
        shouldThrow<AssertionError> { event.shouldNotContainIdentity("a", "b") }
        shouldThrow<AssertionError> { event.shouldContainIdentity("a", "c") }

        event.shouldContainIdentity("c.a", "b")
        event.shouldNotContainIdentity("c.a", "d")
        shouldThrow<AssertionError> { event.shouldNotContainIdentity("c.a", "b") }
        shouldThrow<AssertionError> { event.shouldContainIdentity("c.a", "d") }

        event shouldHaveIdentity complexMap
        event shouldNotHaveIdentity buildJsonObject { put("a", "b") }
        event shouldNotHaveIdentity buildJsonObject { }

        shouldThrow<AssertionError> { event shouldNotHaveIdentity complexMap }
        shouldThrow<AssertionError> { event shouldHaveIdentity buildJsonObject { put("a", "b") } }
        shouldThrow<AssertionError> { event shouldHaveIdentity buildJsonObject { } }
    }

    test("Should have auth") {
        event.shouldContainAuth("a", "b")
        event.shouldNotContainAuth("a", "c")
        shouldThrow<AssertionError> { event.shouldNotContainAuth("a", "b") }
        shouldThrow<AssertionError> { event.shouldContainAuth("a", "c") }

        event.shouldContainAuth("c.a", "b")
        event.shouldNotContainAuth("c.a", "d")
        shouldThrow<AssertionError> { event.shouldNotContainAuth("c.a", "b") }
        shouldThrow<AssertionError> { event.shouldContainAuth("c.a", "d") }

        event shouldHaveAuth complexMap
        event shouldNotHaveAuth buildJsonObject { put("a", "b") }
        event shouldNotHaveAuth buildJsonObject { }

        shouldThrow<AssertionError> { event shouldNotHaveAuth complexMap }
        shouldThrow<AssertionError> { event shouldHaveAuth buildJsonObject { put("a", "b") } }
        shouldThrow<AssertionError> { event shouldHaveAuth buildJsonObject { } }
    }

    test("Should have metadata") {
        event.shouldContainMetadata("a", "b")
        event.shouldNotContainMetadata("a", "c")
        shouldThrow<AssertionError> { event.shouldNotContainMetadata("a", "b") }
        shouldThrow<AssertionError> { event.shouldContainMetadata("a", "c") }

        event.shouldContainMetadata("c.a", "b")
        event.shouldNotContainMetadata("c.a", "d")
        shouldThrow<AssertionError> { event.shouldNotContainMetadata("c.a", "b") }
        shouldThrow<AssertionError> { event.shouldContainMetadata("c.a", "d") }

        event shouldHaveMetadata complexMap
        event shouldNotHaveMetadata buildJsonObject { put("a", "b") }
        event shouldNotHaveMetadata buildJsonObject { }

        shouldThrow<AssertionError> { event shouldNotHaveMetadata complexMap }
        shouldThrow<AssertionError> { event shouldHaveMetadata buildJsonObject { put("a", "b") } }
        shouldThrow<AssertionError> { event shouldHaveMetadata buildJsonObject { } }
    }

    test("Should have userId") {
        val eventWithUserId = event.copy(identity = buildJsonObject { put("userId", 42) })

        eventWithUserId shouldHaveUserId 42
        eventWithUserId shouldNotHaveUserId 43

        shouldThrow<AssertionError> { eventWithUserId shouldNotHaveUserId 42 }
        shouldThrow<AssertionError> { eventWithUserId shouldHaveUserId 43 }
    }

    test("Should have origin") {
        val eventWithOrigin = event.copy(metadata = buildJsonObject { put("origin", "foo") })

        eventWithOrigin shouldHaveOrigin "foo"
        eventWithOrigin shouldNotHaveOrigin "bar"

        shouldThrow<AssertionError> { eventWithOrigin shouldNotHaveOrigin "foo" }
        shouldThrow<AssertionError> { eventWithOrigin shouldHaveOrigin "bar" }
    }
})

private val complexMap: JsonObject = buildJsonObject {
    put("a", "b")
    putJsonObject("c") { put("a", "b") }
}
