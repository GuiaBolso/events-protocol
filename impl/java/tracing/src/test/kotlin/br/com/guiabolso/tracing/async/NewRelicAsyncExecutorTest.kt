package br.com.guiabolso.tracing.async

import br.com.guiabolso.tracing.engine.TracerEngine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import java.io.Closeable
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NewRelicAsyncExecutorTest {

    @Test
    fun `should execute an async task tracing it`() {
        val pool = Executors.newSingleThreadExecutor()
        val executor = NewRelicAsyncExecutor()
        val tracer: TracerEngine<*> = mock()
        val closeable: Closeable = mock()

        whenever(tracer.extractContext()).thenReturn("context")
        whenever(tracer.withContext("context")).thenReturn(closeable)

        val result = executor.executeAsync(tracer, pool) {
            "someWork"
        }

        pool.shutdown()

        assertEquals("someWork", result.get())
        verify(tracer, times(1)).extractContext()
        verify(tracer, times(1)).withContext("context")
        verify(closeable, times(1)).close()
    }

    @Test
    fun `should execute an async callable tracing it`() {
        val pool = Executors.newSingleThreadExecutor()
        val executor = NewRelicAsyncExecutor()
        val tracer: TracerEngine<*> = mock()
        val closeable: Closeable = mock()

        whenever(tracer.extractContext()).thenReturn("context")
        whenever(tracer.withContext("context")).thenReturn(closeable)

        val result = executor.executeAsync(tracer, pool, (Callable<String> {
            "someWork"
        }))

        pool.shutdown()

        assertEquals("someWork", result.get())
        verify(tracer, times(1)).extractContext()
        verify(tracer, times(1)).withContext("context")
        verify(closeable, times(1)).close()
    }
}
