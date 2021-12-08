package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.BypassedException
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BypassExceptionHandlerTest {

    @Test
    fun `should only throws the exception`(): Unit = runBlocking {
        val handler = BypassExceptionHandler(false)
        val exception = TestException()

        assertThrows<TestException> {
            runBlocking { handler.handleException(exception, mockk(), mockk()) }
        }
    }

    @Test
    fun `should throws the exception wrapped`(): Unit = runBlocking {
        val handler = BypassExceptionHandler(true)
        val exception = TestException()
        val event: RequestEvent = mockk()

        val ex = assertThrows<BypassedException> {
            runBlocking { handler.handleException(exception, event, mockk()) }
        }

        assertEquals(exception, ex.exception)
        assertEquals(event, ex.request)
    }

    class TestException : RuntimeException()
}
