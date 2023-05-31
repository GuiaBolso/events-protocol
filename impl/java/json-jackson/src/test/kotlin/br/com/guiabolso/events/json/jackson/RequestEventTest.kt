package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.model.RequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RequestEventTest : StringSpec({

    "serialize response event" {
        val responseEvent = createResponseEvent()
        testAdapter.toJson(responseEvent) shouldBe responseEvent.json
    }

    "serialize request event" {
        val requestEvent = createRequestEvent()
        testAdapter.toJson(requestEvent) shouldBe requestEvent.json
    }

    "read request event" {
        val requestEvent = createRequestEvent()
        val resultEvent = testAdapter.fromJson(requestEvent.json, RequestEvent::class.java)
        resultEvent shouldBe requestEvent
    }
})
