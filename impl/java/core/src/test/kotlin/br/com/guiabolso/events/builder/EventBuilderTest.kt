package br.com.guiabolso.events.builder

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.event
import br.com.guiabolso.events.builder.EventBuilder.Companion.eventNotFound
import br.com.guiabolso.events.builder.EventBuilder.Companion.redirectFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseEvent
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventThreadContextManager.withContext
import br.com.guiabolso.events.exception.MissingEventInformationException
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.int
import br.com.guiabolso.events.json.primitiveNode
import br.com.guiabolso.events.json.toPrimitiveNode
import br.com.guiabolso.events.json.treeNode
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EventBuilderTest {

    @Test
    fun testCreateEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }

        assertEquals("id", event.id)
        assertEquals("flowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertTrue(event.payload.primitiveNode.isNumber)
        assertEquals(42, event.payload.primitiveNode.int)
        assertEquals(TreeNode(), event.auth)
        assertEquals(TreeNode(), event.identity)
        assertEquals(TreeNode(), event.metadata)
    }

    @Test
    fun testCreateEventWithIdAndFlowIdForward() {
        val event = withContext(EventContext("id", "flowId")).use {
            event {
                name = "event"
                version = 1
                payload = 42
            }
        }

        assertEquals("id", event.id)
        assertEquals("flowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertTrue(event.payload.primitiveNode.isNumber)
        assertEquals(42, event.payload.primitiveNode.int)
        assertEquals(TreeNode(), event.auth)
        assertEquals(TreeNode(), event.identity)
        assertEquals(TreeNode(), event.metadata)
    }

    @Test
    fun testCreateEventWithIdAndFlowIdForwardOverwritten() {
        val event = withContext(EventContext("id", "flowId")).use {
            event {
                name = "event"
                id = "otherId"
                flowId = "otherFlowId"
                version = 1
                payload = 42
            }
        }

        assertEquals("otherId", event.id)
        assertEquals("otherFlowId", event.flowId)
        assertEquals("event", event.name)
        assertEquals(1, event.version)
        assertTrue(event.payload is PrimitiveNode)
        assertEquals(42, event.payload.primitiveNode.int)
        assertEquals(TreeNode(), event.auth)
        assertEquals(TreeNode(), event.identity)
        assertEquals(TreeNode(), event.metadata)
    }

    @Test
    fun testCreateEventWithoutIdAndFlowId() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                name = "event"
                version = 1
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutName() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                version = 1
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutVersion() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                name = "event"
                payload = 42
            }
        }
    }

    @Test
    fun testCreateWithoutPayload() {
        assertThrows(MissingEventInformationException::class.java) {
            event {
                id = "id"
                flowId = "flowId"
                name = "event"
            }
        }
    }

    @Test
    fun testResponseEvent() {
        val response = responseEvent {
            id = "id"
            flowId = "flowId"
            name = "event:response"
            version = 1
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertTrue(response.payload is PrimitiveNode)
        assertEquals(84, response.payload.primitiveNode.int)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testResponseForEvent() = runBlocking {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = responseFor(event) {
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertTrue(response.payload is PrimitiveNode)
        assertEquals(84, response.payload.primitiveNode.int)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testResponseForEventWithIdAndFlowIdForward() = runBlocking {
        val event = withContext(EventContext("id", "flowId")).use {
            event {
                name = "event"
                version = 1
                payload = 42
            }
        }
        val response = responseFor(event) {
            payload = 84
        }

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertTrue(response.payload is PrimitiveNode)
        assertEquals(84, response.payload.primitiveNode.int)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testResponseForEventWithIdAndFlowIdForwardOverwritten() = runBlocking {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = responseFor(event) {
            id = "otherId"
            flowId = "otherFlowId"
            payload = 84
        }

        assertEquals("otherId", response.id)
        assertEquals("otherFlowId", response.flowId)
        assertEquals("event:response", response.name)
        assertEquals(1, response.version)
        assertTrue(response.payload is PrimitiveNode)
        assertEquals(84, response.payload.primitiveNode.int)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testResponseForEventWithoutIdAndFlowId(): Unit = runBlocking {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            runBlocking {
                responseFor(event) {
                    id = null
                    flowId = null
                    payload = 84
                }
            }
        }
    }

    @Test
    fun testResponseForEventWithoutName() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            runBlocking {
                responseFor(event) {
                    name = null
                    payload = 84
                }
            }
        }
    }

    @Test
    fun testResponseForEventWithoutVersion() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            runBlocking {
                responseFor(event) {
                    version = null
                    payload = 84
                }
            }
        }
    }

    @Test
    fun testResponseForEventWithoutPayload() {
        assertThrows(MissingEventInformationException::class.java) {
            val event = event {
                name = "event"
                version = 1
                payload = 42
            }
            runBlocking {
                responseFor(event) {
                }
            }
        }
    }

    @Test
    fun testCreateErrorResponseEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = errorFor(event, EventErrorType.Generic, EventMessage("code", emptyMap()))

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:error", response.name)
        assertEquals(1, response.version)
        assertEquals(mapper.toJsonTree(EventMessage("code", emptyMap())), response.payload)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun `test create redirect event`() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }

        val redirectURL = "https://www.google.com.br"
        val redirectPayload = RedirectPayload(url = redirectURL)
        val response = redirectFor(event, redirectPayload)

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("event:redirect", response.name)
        assertEquals(1, response.version)
        assertEquals(redirectURL, response.payload.treeNode["url"]?.primitiveNode?.value)
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testCreateEventNotFoundResponseEvent() {
        val event = event {
            id = "id"
            flowId = "flowId"
            name = "event"
            version = 1
            payload = 42
        }
        val response = eventNotFound(event)

        assertEquals("id", response.id)
        assertEquals("flowId", response.flowId)
        assertEquals("eventNotFound", response.name)
        assertEquals(1, response.version)
        assertEquals(
            mapper.toJsonTree(
                EventMessage(
                    code = "NO_EVENT_HANDLER_FOUND",
                    parameters = mapOf(
                        "event" to event.name.toPrimitiveNode(),
                        "version" to event.version.toPrimitiveNode()
                    )
                )
            ),
            response.payload
        )
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }

    @Test
    fun testCreateBadProtocolResponseEvent() {
        val response = badProtocol(EventMessage("INVALID_COMMUNICATION_PROTOCOL", emptyMap()))

        assertNotNull(response.id)
        assertNotNull(response.flowId)
        assertEquals(BadProtocol.typeName, response.name)
        assertEquals(1, response.version)
        assertEquals(
            mapper.toJsonTree(EventMessage("INVALID_COMMUNICATION_PROTOCOL", emptyMap())),
            response.payload
        )
        assertEquals(TreeNode(), response.auth)
        assertEquals(TreeNode(), response.identity)
        assertEquals(TreeNode(), response.metadata)
    }
}
