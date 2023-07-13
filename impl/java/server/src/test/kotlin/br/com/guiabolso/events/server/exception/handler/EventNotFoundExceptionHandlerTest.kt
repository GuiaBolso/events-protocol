package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.json.JsonAdapterProducer.mapper
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.context
import br.com.guiabolso.events.server.exception.EventNotFoundException
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
    private val exceptionHandler = EventNotFoundExceptionHandler(eventBuilder = EventBuilder(mapper))

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

        val responseEvent = exceptionHandler.handleException(exception, requestEvent.context(), tracer)
        assertEquals(EventErrorType.EventNotFound, responseEvent.getErrorType())

        val message = responseEvent.payloadAs<EventMessage>(mapper)
        assertEquals("NO_EVENT_HANDLER_FOUND", message.code)
        assertEquals(setOf("event", "version"), message.parameters.keys)
        assertEquals("eventName", message.parameters["event"])
        assertEquals(1, message.parameters["version"].toString().toDouble().toInt())

        verify { tracer.notifyError(exception, false) }
    }
}
