package br.com.guiabolso.events.json.moshi.br.com.guiabolso.events.json.moshi.adapter

import br.com.guiabolso.events.json.moshi.adapter.EventProtocolAdapter
import br.com.guiabolso.events.json.moshi.adapter.JsonNodeAdapter
import br.com.guiabolso.events.json.moshi.createRequestEvent
import br.com.guiabolso.events.json.moshi.createResponseEvent
import br.com.guiabolso.events.json.moshi.json
import br.com.guiabolso.events.json.moshi.moshi
import br.com.guiabolso.events.json.moshi.noNullAdapterFor
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

class EventProtocolAdapterTest : StringSpec({
    val requestEventAdapter = EventProtocolAdapter<RequestEvent>(
        delegate = moshi.noNullAdapterFor(),
        jsonNodeAdapter = JsonNodeAdapter(moshi)
    )

    "should throws when try to serialize a null event"{
        shouldThrow<IllegalStateException> { requestEventAdapter.toJson(null) }
            .shouldHaveMessage("Bad protocol message, trying to serialize a null Event")
    }

    "should write request event successfully" {
        val requestEvent = createRequestEvent()
        requestEventAdapter.toJson(requestEvent) shouldBe requestEvent.json
    }

    "should write response event successfully" {
        val requestEvent = createResponseEvent()
        val responseEventAdapter = EventProtocolAdapter<ResponseEvent>(
            delegate = moshi.noNullAdapterFor(),
            jsonNodeAdapter = JsonNodeAdapter(moshi)
        )

        responseEventAdapter.toJson(requestEvent) shouldBe requestEvent.json
    }
})
