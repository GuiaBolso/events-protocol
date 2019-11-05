package br.com.guiabolso.events.model

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.MapperHolder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EventTest {

    @Test
    fun testJsonNullUserIdEvent() {
        val identity = MapperHolder.mapper.fromJson("""{ "userId": null }""", JsonObject::class.java)
        val event = EventBuilderForTest.buildRequestEvent().copy(identity = identity)

        Assertions.assertNull(event.userId)
    }

    @Test
    fun testJsonNullOriginEvent() {
        val metadata = MapperHolder.mapper.fromJson("""{ "origin": null }""", JsonObject::class.java)
        val event = EventBuilderForTest.buildRequestEvent().copy(metadata = metadata)

        Assertions.assertNull(event.origin)
    }

    @Test
    fun testNotNullUserIdEvent() {
        val identity = MapperHolder.mapper.fromJson("""{ "userId": 123987 }""", JsonObject::class.java)
        val event = EventBuilderForTest.buildRequestEvent().copy(identity = identity)

        Assertions.assertEquals(123987L, event.userId)
    }

    @Test
    fun testNotNullOriginEvent() {
        val metadata = MapperHolder.mapper.fromJson("""{ "origin": "east" }""", JsonObject::class.java)
        val event = EventBuilderForTest.buildRequestEvent().copy(metadata = metadata)

        Assertions.assertEquals("east", event.origin)
    }

}