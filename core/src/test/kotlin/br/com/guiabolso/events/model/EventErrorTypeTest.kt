package br.com.guiabolso.events.model

import br.com.guiabolso.events.model.EventErrorType.*
import br.com.guiabolso.events.model.EventErrorType.Companion.getErrorType
import org.junit.Assert.assertEquals
import org.junit.Test

class EventErrorTypeTest {

    @Test
    fun testErrorMapping() {
        assertEquals(Generic::class.java, getErrorType("error")::class.java)
        assertEquals("error", getErrorType("error").typeName)

        assertEquals(NotFound::class.java, getErrorType("notFound")::class.java)
        assertEquals("notFound", getErrorType("notFound").typeName)

        assertEquals(Unknown::class.java, getErrorType("somethingElse")::class.java)
        assertEquals("somethingElse", getErrorType("somethingElse").typeName)
    }

}