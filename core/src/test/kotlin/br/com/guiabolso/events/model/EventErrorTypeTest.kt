package br.com.guiabolso.events.model

import br.com.guiabolso.events.model.EventErrorType.*
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import org.junit.jupiter.api.Assertions.assertEquals
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

        assertEquals(Unknown("somethingElse"), getErrorType("somethingElse"))
        assertEquals("somethingElse", getErrorType("somethingElse").typeName)
    }

}