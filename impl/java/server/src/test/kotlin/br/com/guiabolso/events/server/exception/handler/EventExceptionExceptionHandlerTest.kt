package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.exception.EventException
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.handler.EventExceptionExceptionHandler.handleException
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.utils.ExceptionUtils
import datadog.trace.api.DDTags
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventExceptionExceptionHandlerTest {

    @Test
    fun `should handle the exception and notify tracer properly`(): Unit = runBlocking {
        val exception = TestException()
        val requestEvent: RequestEvent = mockk {
            every { id } returns "id"
            every { flowId } returns "flowId"
            every { name } returns "eventName"
            every { version } returns 1
        }
        val tracer: Tracer = mockk()

        every { tracer.notifyRootError("CODE", mapOf("some" to "parameter"), false) } just Runs
        every { tracer.addRootProperty(DDTags.ERROR_TYPE, "CODE") } just Runs
        every { tracer.addRootProperty(DDTags.ERROR_STACK, ExceptionUtils.getStackTrace(exception)) } just Runs

        val responseEvent = handleException(exception, requestEvent, tracer)
        assertEquals(EventErrorType.Generic, responseEvent.getErrorType())

        val message = responseEvent.payloadAs<EventMessage>()
        assertEquals("CODE", message.code)
        assertEquals(mapOf("some" to "parameter"), message.parameters)

        verify { tracer.notifyRootError("CODE", mapOf("some" to "parameter"), false) }
        verify { tracer.addRootProperty(DDTags.ERROR_TYPE, "CODE") }
        verify { tracer.addRootProperty(DDTags.ERROR_STACK, ExceptionUtils.getStackTrace(exception)) }
    }

    private class TestException : EventException(
        code = "CODE",
        parameters = mapOf("some" to "parameter")
    )
}
