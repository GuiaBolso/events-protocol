package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.async.AsyncExecutor
import br.com.guiabolso.tracing.engine.TracerEngine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import org.junit.jupiter.api.Test

class TracerImplTest {

    private val mockTracer: TracerEngine<*> = mock()
    private val mockExecutorService: ExecutorService = mock()
    private val mockExecutor: AsyncExecutor = mock()
    private val tracer = TracerImpl(mockTracer, mockExecutor)

    @Test
    fun `should delegate setOperationName`() {
        tracer.setOperationName("operationName")

        verify(mockTracer, times(1)).setOperationName("operationName")
    }

    @Test
    fun `should delegate addProperty for string`() {
        tracer.addProperty("key", "value")

        verify(mockTracer, times(1)).addProperty("key", "value")
    }

    @Test
    fun `should delegate addProperty for number`() {
        tracer.addProperty("key", 10)

        verify(mockTracer, times(1)).addProperty("key", 10)
    }

    @Test
    fun `should delegate addProperty for boolean`() {
        tracer.addProperty("key", true)

        verify(mockTracer, times(1)).addProperty("key", true)
    }

    @Test
    fun `should delegate executeAsync`() {
        val task: () -> String = { "batata" }
        tracer.executeAsync(mockExecutorService, task)

        verify(mockExecutor, times(1)).executeAsync(mockTracer, mockExecutorService, task)
    }

    @Test
    fun `should delegate executeAsync callable`() {
        val task: Callable<String> = (Callable { "batata" })
        tracer.executeAsync(mockExecutorService, task)

        verify(mockExecutor, times(1)).executeAsync(mockTracer, mockExecutorService, task)
    }

    @Test
    fun `should delegate noticeError`() {
        val exception = RuntimeException(":]")
        tracer.notifyError(exception, false)

        verify(mockTracer, times(1)).notifyError(exception, false)
    }

    @Test
    fun `should delegate noticeError with message`() {
        tracer.notifyError("error", emptyMap(), false)

        verify(mockTracer, times(1)).notifyError("error", emptyMap(), false)
    }

    @Test
    fun `should delegate clear`() {
        tracer.clear()

        verify(mockTracer, times(1)).clear()
    }
}
