package br.com.guiabolso.events.server

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlockingEventProcessorTest {

    @Test
    fun `should delegate to SuspendingEventProcessor`() {
        val delegate = mockk<SuspendingEventProcessor>()
        val processor = BlockingEventProcessor(delegate)

        coEvery { delegate.processEvent("event") } returns "response"

        assertEquals("response", processor.processEvent("event"))

        coVerify(exactly = 1) { delegate.processEvent("event") }
    }

}
