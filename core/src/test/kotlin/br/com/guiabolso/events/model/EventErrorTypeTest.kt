package br.com.guiabolso.events.model

import br.com.guiabolso.events.model.EventErrorType.*
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import org.junit.Assert.assertEquals
import org.junit.Test

class EventErrorTypeTest {

    @Test
    fun testErrorMapping() {
        assertEquals(Generic, getErrorType("error"))
        assertEquals("error", getErrorType("error").typeName)

        assertEquals(NotFound, getErrorType("notFound"))
        assertEquals("notFound", getErrorType("notFound").typeName)

        assertEquals(Unknown("somethingElse"), getErrorType("somethingElse"))
        assertEquals("somethingElse", getErrorType("somethingElse").typeName)
    }

}