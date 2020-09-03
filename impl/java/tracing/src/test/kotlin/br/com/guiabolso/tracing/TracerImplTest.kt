package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.async.AsyncExecutor
import br.com.guiabolso.tracing.engine.TracerEngine
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Test

class TracerImplTest {

    private val mockTracer: TracerEngine<*> = mockk(relaxed = true)
    private val mockExecutorService: ExecutorService = mockk()
    private val mockSchedulerService: ScheduledExecutorService = mockk()
    private val mockExecutor: AsyncExecutor = mockk(relaxed = true)
    private val tracer = TracerImpl(mockTracer, mockExecutor)

    @Test
    fun `should delegate setOperationName`() {
        tracer.setOperationName("operationName")

        verify(exactly = 1) { mockTracer.setOperationName("operationName") }
    }

    @Test
    fun `should delegate addProperty for string`() {
        tracer.addProperty("key", "value")

        verify(exactly = 1) { mockTracer.addProperty("key", "value") }
    }

    @Test
    fun `should delegate addRootProperty for string`() {
        tracer.addRootProperty("key", "value")

        verify(exactly = 1) { mockTracer.addRootProperty("key", "value") }
    }

    @Test
    fun `should delegate addProperty for number`() {
        tracer.addProperty("key", 10)

        verify(exactly = 1) { mockTracer.addProperty("key", 10) }
    }

    @Test
    fun `should delegate addRootProperty for number`() {
        tracer.addRootProperty("key", 10)

        verify(exactly = 1) { mockTracer.addRootProperty("key", 10) }
    }

    @Test
    fun `should delegate addProperty for boolean`() {
        tracer.addProperty("key", true)

        verify(exactly = 1) { mockTracer.addProperty("key", true) }
    }

    @Test
    fun `should delegate addRootProperty for boolean`() {
        tracer.addRootProperty("key", true)

        verify(exactly = 1) { mockTracer.addRootProperty("key", true) }
    }

    @Test
    fun `should delegate executeAsync callable`() {
        val task: Callable<String> = (Callable { "batata" })
        tracer.executeAsync(mockExecutorService, task)

        verify(exactly = 1) { mockExecutor.executeAsync(mockTracer, mockExecutorService, task) }
    }

    @Test
    fun `should delegate schedule callable`() {
        val task: Callable<String> = (Callable { "batata" })
        tracer.schedule(mockSchedulerService, task, 1, TimeUnit.SECONDS)

        verify(exactly = 1) { mockExecutor.schedule(mockTracer, mockSchedulerService, task, 1, TimeUnit.SECONDS) }
    }

    @Test
    fun `should delegate noticeError`() {
        val exception = RuntimeException(":]")
        tracer.notifyError(exception, false)

        verify(exactly = 1) { mockTracer.notifyError(exception, false) }
    }

    @Test
    fun `should delegate noticeError with message`() {
        tracer.notifyError("error", emptyMap(), false)

        verify(exactly = 1) { mockTracer.notifyError("error", emptyMap(), false) }
    }

    @Test
    fun `should delegate clear`() {
        tracer.clear()

        verify(exactly = 1) { mockTracer.clear() }
    }
}
