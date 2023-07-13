package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.json.JsonAdapterProducer.mapper
import br.com.guiabolso.events.model.EventErrorType.BadProtocol
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.context
import br.com.guiabolso.tracing.Tracer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BadProtocolExceptionHandlerTest {

    val exceptionHandler = BadProtocolExceptionHandler(EventBuilder(mapper))

    @Test
    fun `should handle the exception and notify tracer properly`(): Unit = runBlocking {
        val exception = EventValidationException("someProperty")
        val requestEvent: RequestEvent = mockk {
            every { id } returns "id"
            every { flowId } returns "flowId"
            every { name } returns "eventName"
            every { version } returns 1
        }
        val tracer: Tracer = mockk()

        every { tracer.notifyError(exception, false) } just Runs

        val eventContext = requestEvent.context(mapper)

        val responseEvent = exceptionHandler.handleException(exception, eventContext, tracer)
        assertEquals(BadProtocol, responseEvent.getErrorType())

        val message = responseEvent.payloadAs<EventMessage>(mapper)
        assertEquals("INVALID_COMMUNICATION_PROTOCOL", message.code)
        assertEquals(mapOf("propertyName" to "someProperty"), message.parameters)

        verify { tracer.notifyError(exception, false) }
    }
}
