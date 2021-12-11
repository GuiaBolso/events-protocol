package br.com.guiabolso.events

import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.gson.GsonParser
import br.com.guiabolso.events.model.EventErrorType.BadRequest
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.test.beError
import br.com.guiabolso.events.test.beSuccess
import br.com.guiabolso.events.test.shouldHaveErrorType
import br.com.guiabolso.events.test.shouldHaveName
import br.com.guiabolso.events.test.shouldHavePayload
import br.com.guiabolso.events.test.shouldNotHavePayload
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.ktor.application.Application
import io.ktor.http.HttpMethod.Companion.Post
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID.randomUUID

class EventsTest : ShouldSpec({

    beforeSpec {
        MapperHolder.mapper = GsonParser()
    }

    should("Have the same response for /events and /events/") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(testEvent) }.response
            val response2 = handleRequest(Post, "/events") { setBody(testEvent) }.response

            response.content!! shouldBe response2.content!!
        }
    }

    should("Handle a successful event request->response") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(testEvent) }.response

            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:response"
                it shouldHavePayload mapOf("answer" to 42)
            }
        }
    }

    should("Handle a successful event with default UTF-8 charset request->response") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(testEncodingEvent) }.response

            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:encoding:response"
                it shouldHavePayload mapOf("answer" to mapOf("text" to "éàçãõ£¥ÄĀ"))
            }
        }
    }

    should("Handle a successful event with non default charset request") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") {
                setBody(testEncodingEvent)
                addHeader("Content-Type", "application/json; charset=ISO_8859_1")
            }.response

            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:encoding:response"
                it shouldNotHavePayload mapOf("answer" to mapOf("text" to "éàçãõ£¥ÄĀ"))
            }
        }
    }

    should("Respond to an unregistered event with failure") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(eventNotFound) }.response
            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beError()
                it shouldHaveName "eventNotFound"
            }
        }
    }

    should("Respond when an error occurs in the event") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(testEventErr) }.response
            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beError()
                it shouldHaveName "test:err:event:badRequest"
                it shouldHaveErrorType BadRequest
                it shouldHavePayload mapOf("code" to "SOME_ERROR", "parameters" to emptyMap<String, String>())
            }
        }
    }

    should("Capture registered exceptions") {
        withTestApplication({ testModule() }) {
            val response = handleRequest(Post, "/events/") { setBody(testEventHandledErr) }.response
            assertSoftly(mapper.fromJson(response.content!!, ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:err:event:with:exception:response"
                it shouldHavePayload mapOf("OK" to "all is fine!")
            }
        }
    }

    should("Break when tries to register events feature twice") {
        val e = shouldThrow<IllegalStateException> {
            withTestApplication({ brokenTestModule() }) {}
        }

        e shouldHaveMessage "Cannot initialize Events more than once!"
    }
})

fun Application.testModule() {

    events {
        event("test:event", 1) {
            responseFor(it) {
                payload = mapOf("answer" to 42)
            }
        }

        event("test:event:encoding", 1) {
            responseFor(it) {
                payload = mapOf("answer" to it.payload)
            }
        }

        event("test:err:event", 1) {
            errorFor(it, BadRequest, EventMessage("SOME_ERROR", emptyMap()))
        }

        event("test:err:event:with:exception", 1) {
            throw IllegalStateException("Oh no!")
        }

        exception(IllegalStateException::class) { _, evt, _ ->
            responseFor(evt) {
                payload = mapOf("OK" to "all is fine!")
            }
        }
    }
}

fun Application.brokenTestModule() {
    events {
        event("test:event", 1) {
            responseFor(it) {
                payload = mapOf("answer" to 42)
            }
        }
    }
    events {
        event("another:event", 1) {
            responseFor(it) {
                payload = mapOf("answer" to 42)
            }
        }
    }
}

private val testEvent =
    """ {
    "name": "test:event",
    "version": 1,
    "id": "${randomUUID()}",
    "flowId": "${randomUUID()}",
    "payload": {},
    "identity": {},
    "metadata": {},
    "auth": {}
}
    """.trimIndent()

private val eventNotFound =
    """ {
    "name": "non:existent",
    "version": 1,
    "id": "${randomUUID()}",
    "flowId": "${randomUUID()}",
    "payload": {},
    "identity": {},
    "metadata": {},
    "auth": {}
}
    """.trimIndent()

private val testEventErr =
    """ {
    "name": "test:err:event",
    "version": 1,
    "id": "${randomUUID()}",
    "flowId": "${randomUUID()}",
    "payload": {},
    "identity": {},
    "metadata": {},
    "auth": {}
}
    """.trimIndent()

private val testEventHandledErr =
    """ {
    "name": "test:err:event:with:exception",
    "version": 1,
    "id": "${randomUUID()}",
    "flowId": "${randomUUID()}",
    "payload": {},
    "identity": {},
    "metadata": {},
    "auth": {}
}
    """.trimIndent()

private val testEncodingEvent =
    """ {
    "name": "test:event:encoding",
    "version": 1,
    "id": "${randomUUID()}",
    "flowId": "${randomUUID()}",
    "payload": { "text" : "éàçãõ£¥ÄĀ" },
    "identity": {},
    "metadata": {},
    "auth": {}
}
    """.trimIndent()
