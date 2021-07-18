package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.EventNotFoundException
import br.com.guiabolso.events.server.exception.handler.EventNotFoundExceptionHandler.handleException
import br.com.guiabolso.tracing.Tracer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EventNotFoundExceptionHandlerTest {

    @Test
    fun `should handle the exception and notify tracer properly`(): Unit = runBlocking {
        val exception = EventNotFoundException()
        val requestEvent: RequestEvent = mockk {
            every { id } returns "id"
            every { flowId } returns "flowId"
            every { name } returns "eventName"
            every { version } returns 1
        }
        val tracer: Tracer = mockk()

        every { tracer.notifyError(exception, false) } just Runs

        val responseEvent = handleException(exception, requestEvent, tracer)
        assertEquals(EventErrorType.EventNotFound, responseEvent.getErrorType())

        val message = responseEvent.payloadAs<EventMessage>()
        assertEquals("NO_EVENT_HANDLER_FOUND", message.code)
        assertEquals(mapOf("event" to "eventName", "version" to 1.0), message.parameters)

        verify { tracer.notifyError(exception, false) }
    }

}