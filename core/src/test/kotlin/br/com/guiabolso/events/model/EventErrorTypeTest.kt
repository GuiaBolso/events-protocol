package br.com.guiabolso.events.model

import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.EventErrorType.BadRequest
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import br.com.guiabolso.events.model.EventErrorType.EventNotFound
import br.com.guiabolso.events.model.EventErrorType.Expired
import br.com.guiabolso.events.model.EventErrorType.Forbidden
import br.com.guiabolso.events.model.EventErrorType.Generic
import br.com.guiabolso.events.model.EventErrorType.NotFound
import br.com.guiabolso.events.model.EventErrorType.ResourceDenied
import br.com.guiabolso.events.model.EventErrorType.Unauthorized
import br.com.guiabolso.events.model.EventErrorType.Unknown
import br.com.guiabolso.events.model.EventErrorType.UserDenied
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventErrorTypeTest {

    @Test
    fun testErrorMapping() {
        assertEquals(Generic, getErrorType("error"))
        assertEquals("error", getErrorType("error").typeName)

        assertEquals(NotFound, getErrorType("notFound"))
        assertEquals("notFound", getErrorType("notFound").typeName)

        assertEquals(EventNotFound, getErrorType("eventNotFound"))
        assertEquals("eventNotFound", getErrorType("eventNotFound").typeName)

        assertEquals(BadRequest, getErrorType("badRequest"))
        assertEquals("badRequest", getErrorType("badRequest").typeName)

        assertEquals(BadProtocol, getErrorType("badProtocol"))
        assertEquals("badProtocol", getErrorType("badProtocol").typeName)

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

}