package br.com.guiabolso.events.json.kserialization.serializers

import br.com.guiabolso.events.json.kserialization.helpers.createRequestEvent
import br.com.guiabolso.events.json.kserialization.helpers.createResponseEvent
import br.com.guiabolso.events.json.kserialization.helpers.json
import br.com.guiabolso.events.model.RequestEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

class EventSerializerTest : StringSpec({
    val jsonAdapter = Json {
        serializersModule = SerializersModule {
            contextual(JsonNodeSerializer)
            contextual(ArrayNodeSerializer)
            contextual(TreeNodeSerializer)
            contextual(PrimitiveNodeSerializer)
            contextual(JsonNullSerializer)
            contextual(JsonLiteralSerializer)
            contextual(EventSerializer("RequestEvent", RequestEventCreator))
            contextual(EventSerializer("ResponseEvent", ResponseEventCreator))
        }
    }

    "should encode/decode request event successfully" {
        val requestEvent = createRequestEvent()

        val encodedJson = jsonAdapter.encodeToString(requestEvent)
        encodedJson shouldBe requestEvent.json

        jsonAdapter.decodeFromString<RequestEvent>(encodedJson) shouldBe requestEvent
    }

    "should encode/decode response event successfully" {
        val requestEvent = createResponseEvent()
        jsonAdapter.encodeToString(requestEvent) shouldBe requestEvent.json
    }
})
