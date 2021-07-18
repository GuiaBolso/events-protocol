package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRawRequestEvent
import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventCoroutineContextForwarder
import br.com.guiabolso.events.context.EventThreadContextManager
import br.com.guiabolso.events.exception.EventValidationException
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.EventNotFoundException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandler
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.StrictEventValidator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RawEventProcessorTest {

    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry
    private lateinit var eventValidator: StrictEventValidator
    private lateinit var processor: RawEventProcessor

    @BeforeEach
    fun setup() {
        eventHandlerRegistry = mockk()
        exceptionHandlerRegistry = mockk()
        eventValidator = mockk()

        processor = RawEventProcessor(eventHandlerRegistry, exceptionHandlerRegistry, eventValidator = eventValidator)
    }

    @Test
    fun `should execute the event with contexts set`(): Unit = runBlocking {
        val rawEvent = buildRawRequestEvent()
        val request = mockk<RequestEvent> {
            every { id } returns "id"
            every { flowId } returns "flowId"
            every { name } returns "eventName"
            every { version } returns 1
            every { userIdAsString } returns "42"
            every { origin } returns "origin"
        }
        val response = mockk<ResponseEvent>()
        val handler = mockk<EventHandler>()

        every { eventValidator.validateAsRequestEvent(rawEvent) } returns request
        every { eventHandlerRegistry.eventHandlerFor("eventName", 1) } returns handler
        coEvery { handler.handle(request) } answers {
            assertEquals(EventContext("id", "flowId"), EventThreadContextManager.current)
            assertEquals(EventContext("id", "flowId"), EventCoroutineContextForwarder.current)
            response
        }

        assertEquals(response, processor.processEvent(rawEvent))

        verify(exactly = 1) { eventValidator.validateAsRequestEvent(rawEvent) }
        verify(exactly = 1) { eventHandlerRegistry.eventHandlerFor("eventName", 1) }
        coVerify(exactly = 1) { handler.handle(request) }
    }

    @Test
    fun `should route validation exceptions to the ExceptionHandler`(): Unit = runBlocking {
        val rawEvent = buildRawRequestEvent()
        val exception = EventValidationException("")
        val fakeEvent = slot<RequestEvent>()

        every { eventValidator.validateAsRequestEvent(rawEvent) } throws exception
        coEvery {
            exceptionHandlerRegistry.handleException(exception, capture(fakeEvent), DefaultTracer)
        } throws EventValidationException("")

        assertThrows<EventValidationException> {
            runBlocking { processor.processEvent(rawEvent) }
        }

        verify(exactly = 1) { eventValidator.validateAsRequestEvent(rawEvent) }
        coVerify(exactly = 1) { exceptionHandlerRegistry.handleException(exception, fakeEvent.captured, DefaultTracer) }
    }

    @Test
    fun `should route event not found exception to the ExceptionHandler`(): Unit = runBlocking {
        val rawEvent = buildRawRequestEvent()
        val request = mockk<RequestEvent> {
            every { id } returns "id"
            every { flowId } returns "flowId"
            every { name } returns "eventName"
            every { version } returns 1
            every { userIdAsString } returns "42"
            every { origin } returns "origin"
        }
        val exception = EventNotFoundException()
        val fakeEvent = slot<RequestEvent>()

        every { eventValidator.validateAsRequestEvent(rawEvent) } returns request
        every { eventHandlerRegistry.eventHandlerFor("eventName", 1) } throws exception
        coEvery {
            exceptionHandlerRegistry.handleException(exception, capture(fakeEvent), DefaultTracer)
        } throws EventNotFoundException()

        assertThrows<EventNotFoundException> {
            runBlocking { processor.processEvent(rawEvent) }
        }

        verify(exactly = 1) { eventValidator.validateAsRequestEvent(rawEvent) }
        verify(exactly = 1) { eventHandlerRegistry.eventHandlerFor("eventName", 1) }
        coVerify(exactly = 1) { exceptionHandlerRegistry.handleException(exception, fakeEvent.captured, DefaultTracer) }
    }
}
