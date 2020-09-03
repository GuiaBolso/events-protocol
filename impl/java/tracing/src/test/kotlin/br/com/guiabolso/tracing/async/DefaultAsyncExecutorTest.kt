package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.Closeable
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DefaultAsyncExecutorTest {

    @Test
    fun `should execute an async callable tracing it`() {
        val pool = Executors.newSingleThreadExecutor()
        val executor = DefaultAsyncExecutor()
        val tracer: TracerEngine<*> = mockk()
        val closeable: Closeable = mockk(relaxed = true)

        every { tracer.extractContext() } returns "context"
        every { tracer.withContext("context") } returns closeable

        val result = executor.executeAsync(tracer, pool, (Callable {
            "someWork"
        }))

        pool.shutdown()

        assertEquals("someWork", result.get())
        verify(exactly = 1) { tracer.extractContext() }
        verify(exactly = 1) { tracer.withContext("context") }
        verify(exactly = 1) { closeable.close() }
    }

    @Test
    fun `should schedule a callable tracing it`() {
        val pool = Executors.newScheduledThreadPool(1)
        val executor = DefaultAsyncExecutor()
        val tracer: TracerEngine<*> = mockk()
        val closeable: Closeable = mockk(relaxed = true)

        every { tracer.extractContext() } returns "context"
        every { tracer.withContext("context") } returns closeable

        val result = executor.schedule(tracer, pool, (Callable {
            "someWork"
        }), 10, TimeUnit.MILLISECONDS)

        pool.shutdown()

        assertEquals("someWork", result.get())
        verify(exactly = 1) { tracer.extractContext() }
        verify(exactly = 1) { tracer.withContext("context") }
        verify(exactly = 1) { closeable.close() }
    }
}
