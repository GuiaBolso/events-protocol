package br.com.guiabolso.events.json.gson.adapters

import br.com.guiabolso.events.json.gson.createRequestEvent
import br.com.guiabolso.events.json.gson.createResponseEvent
import br.com.guiabolso.events.json.gson.gson
import br.com.guiabolso.events.json.gson.json
import br.com.guiabolso.events.json.gson.toJsonReader
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class EventTypeAdapterTest : StringSpec({
    val requestEventAdapter = EventTypeAdapter<RequestEvent>(
        jsonNodeAdapter = JsonNodeAdapter,
        readerDelegate = gson.getAdapter(RequestEvent::class.java)
    )

    val responseEventAdapter = EventTypeAdapter<ResponseEvent>(
        jsonNodeAdapter = JsonNodeAdapter,
        readerDelegate = gson.getAdapter(ResponseEvent::class.java)
    )

    "should throws when try to serialize a null event" {
        shouldThrow<IllegalStateException> { requestEventAdapter.toJson(null) }
            .shouldHaveMessage("Bad protocol message, trying to serialize a null Event")
    }

    "should read request event successfully" {
        val request = createRequestEvent()
        requestEventAdapter.read(request.json.toJsonReader()) shouldBe request
    }

    "should write request event successfully" {
        val requestEvent = createRequestEvent()
        requestEventAdapter.toJson(requestEvent) shouldBe requestEvent.json
    }

    "should read response event successfully" {
        val request = createResponseEvent()
        responseEventAdapter.read(request.json.toJsonReader()) shouldBe request
    }

    "should write response event successfully" {
        val requestEvent = createResponseEvent()
        responseEventAdapter.toJson(requestEvent) shouldBe requestEvent.json
    }
})
