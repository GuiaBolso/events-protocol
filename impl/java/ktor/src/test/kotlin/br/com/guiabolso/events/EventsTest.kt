package br.com.guiabolso.events

import br.com.guiabolso.events.builder.EventBuilder.Companion.errorFor
import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.json.moshi.MoshiJsonAdapter
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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.server.application.Application
import io.ktor.server.testing.testApplication
import java.util.UUID.randomUUID

class EventsTest : ShouldSpec({

    beforeSpec {
        MapperHolder.mapper = MoshiJsonAdapter()
    }

    should("Have the same response for /events and /events/") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(testEvent) }
            val response2 = client.post("/events") { setBody(testEvent) }

            response.bodyAsText() shouldBe response2.bodyAsText()
        }
    }

    should("Handle a successful event request->response") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(testEvent) }

            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:response"
                it shouldHavePayload mapOf("answer" to 42)
            }
        }
    }

    should("Handle a successful event with default UTF-8 charset request->response") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(testEncodingEvent) }

            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:encoding:response"
                it shouldHavePayload mapOf("answer" to mapOf("text" to "éàçãõ£¥ÄĀ"))
            }
        }
    }

    should("Handle a successful event with non default charset request") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") {
                setBody(testEncodingEvent)
                header("Content-Type", "application/json; charset=ISO_8859_1")
            }

            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:event:encoding:response"
                it shouldNotHavePayload mapOf("answer" to mapOf("text" to "éàçãõ£¥ÄĀ"))
            }
        }
    }

    should("Respond to an unregistered event with failure") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(eventNotFound) }
            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beError()
                it shouldHaveName "eventNotFound"
            }
        }
    }

    should("Respond when an error occurs in the event") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(testEventErr) }
            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beError()
                it shouldHaveName "test:err:event:badRequest"
                it shouldHaveErrorType BadRequest
                it shouldHavePayload mapOf("code" to "SOME_ERROR", "parameters" to emptyMap<String, String>())
            }
        }
    }

    should("Capture registered exceptions") {
        testApplication {
            application { testModule() }
            val response = client.post("/events/") { setBody(testEventHandledErr) }
            assertSoftly(mapper.fromJson(response.bodyAsText(), ResponseEvent::class.java)) {
                it should beSuccess()
                it shouldHaveName "test:err:event:with:exception:response"
                it shouldHavePayload mapOf("OK" to "all is fine!")
            }
        }
    }

    should("Break when tries to register events feature twice") {
        val e = shouldThrow<IllegalStateException> {
            testApplication { application { brokenTestModule() } }
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
