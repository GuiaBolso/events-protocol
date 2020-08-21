package br.com.guiabolso.events.model

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import br.com.guiabolso.events.model.EventErrorType.Expired
import br.com.guiabolso.events.model.EventErrorType.Forbidden
import br.com.guiabolso.events.model.EventErrorType.Generic
import br.com.guiabolso.events.model.EventErrorType.NotFound
import br.com.guiabolso.events.model.EventErrorType.ResourceDenied
import br.com.guiabolso.events.model.EventErrorType.Unauthorized
import br.com.guiabolso.events.model.EventErrorType.Unknown
import br.com.guiabolso.events.model.EventErrorType.UserDenied
import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EventErrorTypeTest {

    @Test
    fun testErrorMapping() {
        assertEquals(Generic, getErrorType("error"))
        assertEquals("error", getErrorType("error").typeName)

        assertEquals(NotFound, getErrorType("notFound"))
        assertEquals("notFound", getErrorType("notFound").typeName)

        assertEquals(Unauthorized, getErrorType("unauthorized"))
        assertEquals("unauthorized", getErrorType("unauthorized").typeName)

        assertEquals(Forbidden, getErrorType("forbidden"))
        assertEquals("forbidden", getErrorType("forbidden").typeName)

        assertEquals(UserDenied, getErrorType("userDenied"))
        assertEquals("userDenied", getErrorType("userDenied").typeName)

        assertEquals(ResourceDenied, getErrorType("resourceDenied"))
        assertEquals("resourceDenied", getErrorType("resourceDenied").typeName)

        assertEquals(Expired, getErrorType("expired"))
        assertEquals("expired", getErrorType("expired").typeName)

        assertEquals(Unknown("somethingElse"), getErrorType("somethingElse"))
        assertEquals("somethingElse", getErrorType("somethingElse").typeName)
    }

    @Test
    fun testJsonNullUserIdEvent() {
        val identity = mapper.parseToJsonElement(
            """
            {
                "userId": null
            }
        """.trimIndent()
        ).jsonObject

        val event = EventBuilderForTest.buildRequestEvent().copy(identity = identity)

        assertNull(event.userId)
    }

    @Test
    fun testJsonNullOriginEvent() {
        val metadata = mapper.parseToJsonElement(
            """
            {
                "origin": null
            }
        """.trimIndent()
        ).jsonObject

        val event = EventBuilderForTest.buildRequestEvent().copy(metadata = metadata)

        assertNull(event.origin)
    }

    @Test
    fun testNotNullUserIdEvent() {
        val identity = mapper.parseToJsonElement(
            """
            {
                "userId": 123987
            }
        """.trimIndent()
        ).jsonObject

        val event = EventBuilderForTest.buildRequestEvent().copy(identity = identity)

        assertEquals(123987L, event.userId)
    }

    @Test
    fun testNotNullOriginEvent() {
        val metadata = mapper.parseToJsonElement(
            """
            {
                "origin": "east"
            }
        """.trimIndent()
        ).jsonObject

        val event = EventBuilderForTest.buildRequestEvent().copy(metadata = metadata)

        assertEquals("east", event.origin)
    }
}
