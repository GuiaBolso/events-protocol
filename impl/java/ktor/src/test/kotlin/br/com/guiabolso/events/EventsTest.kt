package br.com.guiabolso.events

import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.event
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.EventErrorType.BadRequest
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.ResponseEvent
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class EventsTest {

    @Test
    fun `test can respond to event`() {
        withTestApplication({ testModule() }) {
            with(handleRequest(HttpMethod.Post, "/events/") {
                setBody(mapper.toJson(testEvent))
            }) {
                val responseEvent = mapper.fromJson(response.content, ResponseEvent::class.java)

                assertTrue(responseEvent.isSuccess())
            }
            with(handleRequest(HttpMethod.Post, "/events/") {
                setBody(mapper.toJson(eventNotFound))
            }) {
                val responseEvent = mapper.fromJson(response.content, ResponseEvent::class.java)

                assertTrue(responseEvent.isError())
            }
            with(handleRequest(HttpMethod.Post, "/events/") {
                setBody(mapper.toJson(testEventErr))
            }) {
                val responseEvent = mapper.fromJson(response.content, ResponseEvent::class.java)

                assertTrue(responseEvent.isError())
                assertEquals(BadRequest, responseEvent.getErrorType())
            }
        }
    }
}

fun Application.testModule() {

    events {
        event("test:event", 1) {
            responseFor(it) {
                payload = mapOf("answer" to 42)
            }
        }
        event("test:err:event", 1) {
            errorFor(it, BadRequest, EventMessage("SOME_ERROR", emptyMap()))
        }
    }

}

private val testEvent = event {
    name = "test:event"
    version = 1
    id = UUID.randomUUID().toString()
    flowId = UUID.randomUUID().toString()
    payload = emptyMap<String, Any>()
}

private val testEventErr = event {
    name = "test:err:event"
    version = 1
    id = UUID.randomUUID().toString()
    flowId = UUID.randomUUID().toString()
    payload = emptyMap<String, Any>()
}

private val eventNotFound = event {
    name = "non:existent"
    version = 1
    id = UUID.randomUUID().toString()
    flowId = UUID.randomUUID().toString()
    payload = emptyMap<String, Any>()
}
