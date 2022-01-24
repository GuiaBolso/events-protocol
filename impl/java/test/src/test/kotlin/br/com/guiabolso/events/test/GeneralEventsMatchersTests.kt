package br.com.guiabolso.events.test

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class GeneralEventsMatchersTests : FunSpec({

    beforeSpec {
        MapperHolder.mapper = MoshiJsonAdapter()
    }

    val event = EventBuilder.event {
        name = "a:b"
        version = 3
        id = "id"
        flowId = "flowId"
        payload = complexMap
        identity = complexMap
        auth = complexMap
        metadata = complexMap
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
        event shouldNotHavePayload mapOf("a" to "b")
        event shouldNotHavePayload emptyMap()

        shouldThrow<AssertionError> { event shouldNotHavePayload complexMap }
        shouldThrow<AssertionError> { event shouldHavePayload mapOf("a" to "b") }
        shouldThrow<AssertionError> { event shouldHavePayload emptyMap() }
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
        event shouldNotHaveIdentity mapOf("a" to "b")
        event shouldNotHaveIdentity emptyMap()

        shouldThrow<AssertionError> { event shouldNotHaveIdentity complexMap }
        shouldThrow<AssertionError> { event shouldHaveIdentity mapOf("a" to "b") }
        shouldThrow<AssertionError> { event shouldHaveIdentity emptyMap() }
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
        event shouldNotHaveAuth mapOf("a" to "b")
        event shouldNotHaveAuth emptyMap()

        shouldThrow<AssertionError> { event shouldNotHaveAuth complexMap }
        shouldThrow<AssertionError> { event shouldHaveAuth mapOf("a" to "b") }
        shouldThrow<AssertionError> { event shouldHaveAuth emptyMap() }
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
        event shouldNotHaveMetadata mapOf("a" to "b")
        event shouldNotHaveMetadata emptyMap()

        shouldThrow<AssertionError> { event shouldNotHaveMetadata complexMap }
        shouldThrow<AssertionError> { event shouldHaveMetadata mapOf("a" to "b") }
        shouldThrow<AssertionError> { event shouldHaveMetadata emptyMap() }
    }

    test("Should have userId") {
        val eventWithUserId = event.copy(identity = mapOf("userId" to 42).toJsonObject())

        eventWithUserId shouldHaveUserId 42
        eventWithUserId shouldNotHaveUserId 43

        shouldThrow<AssertionError> { eventWithUserId shouldNotHaveUserId 42 }
        shouldThrow<AssertionError> { eventWithUserId shouldHaveUserId 43 }
    }

    test("Should have origin") {
        val eventWithOrigin = event.copy(metadata = mapOf("origin" to "foo").toJsonObject())

        eventWithOrigin shouldHaveOrigin "foo"
        eventWithOrigin shouldNotHaveOrigin "bar"

        shouldThrow<AssertionError> { eventWithOrigin shouldNotHaveOrigin "foo" }
        shouldThrow<AssertionError> { eventWithOrigin shouldHaveOrigin "bar" }
    }
})

private val complexMap = mapOf(
    "a" to "b",
    "c" to mapOf("a" to "b")
)

private fun Map<String, Any?>.toJsonObject(): TreeNode = mapper.toJsonTree(this) as TreeNode
