package br.com.guiabolso.tracing.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExceptionUtilsTest {

    @Test
    fun `should get the exception's stacktrace`() {
        val expected = "java.lang.IllegalArgumentException: Test"
        try {
            throw IllegalArgumentException("Test")
        } catch (e: Exception) {
            assertTrue(ExceptionUtils.getStackTrace(e).startsWith(expected))
        }
    }

    @Test
    fun `should should return null if an exception is thrown`() {
        assertEquals("batata", ExceptionUtils.doNotFail { "batata" })
        assertNull(ExceptionUtils.doNotFail { throw IllegalArgumentException("Test") })
    }
}
