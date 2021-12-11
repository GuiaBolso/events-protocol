package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.EventParsingException
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SuspendingEventProcessorTest {

    private lateinit var exceptionHandler: ExceptionHandlerRegistry
    private lateinit var rawEventProcessor: RawEventProcessor
    private lateinit var processor: SuspendingEventProcessor

    @BeforeEach
    fun setup() {
        exceptionHandler = mockk()
        rawEventProcessor = mockk {
            every { exceptionHandlerRegistry } returns exceptionHandler
            every { tracer } returns mockk()
        }

        processor = SuspendingEventProcessor(rawEventProcessor)
    }

    @Test
    fun `should delegate any valid json to raw event processor`(): Unit = runBlocking {
        coEvery { rawEventProcessor.processEvent(any()) } returns buildResponseEvent()

        val response = processor.processEvent("{}")
        assertEquals(
            "{\"name\":\"event:name:response\",\"version\":1,\"id\":\"id\",\"flowId\":\"flowId\",\"payload\":\"42\",\"identity\":{},\"auth\":{},\"metadata\":{}}",
            response
        )
    }

    @Test
    fun `should route parser exceptions to the ExceptionHandler`(): Unit = runBlocking {
        val exception = slot<RuntimeException>()
        val response = slot<RequestEvent>()

        coEvery {
            exceptionHandler.handleException(
                capture(exception),
                capture(response),
                eq(rawEventProcessor.tracer)
            )
        } throws EventParsingException(null)

        assertThrows<EventParsingException> {
            runBlocking { processor.processEvent("not a json") }
        }

        coVerify(exactly = 1) {
            exceptionHandler.handleException(
                exception.captured,
                response.captured,
                rawEventProcessor.tracer
            )
        }
    }

    @Test
    fun `should route create consider null event as a parser exception then route it to the ExceptionHandler`(): Unit =
        runBlocking {
            val exception = slot<RuntimeException>()
            val response = slot<RequestEvent>()

            coEvery {
                exceptionHandler.handleException(
                    capture(exception),
                    capture(response),
                    eq(rawEventProcessor.tracer)
                )
            } throws EventParsingException(null)

            assertThrows<EventParsingException> {
                runBlocking { processor.processEvent("null") }
            }

            coVerify(exactly = 1) {
                exceptionHandler.handleException(
                    exception.captured,
                    response.captured,
                    rawEventProcessor.tracer
                )
            }
        }
}
