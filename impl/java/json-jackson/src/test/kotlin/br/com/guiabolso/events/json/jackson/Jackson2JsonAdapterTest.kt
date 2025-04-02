package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.model.RequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Jackson2JsonAdapterTest : StringSpec({

    "should decode from input stream" {
        val requestEvent = createRequestEvent()
        testAdapter.fromJson(requestEvent.json, RequestEvent::class.java) shouldBe requestEvent
    }
})
