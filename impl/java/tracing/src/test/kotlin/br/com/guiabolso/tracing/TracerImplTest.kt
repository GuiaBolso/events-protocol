package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.engine.TracerEngine
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class TracerImplTest {

    private val mockEngine1: TracerEngine = mockk(relaxed = true)
    private val mockEngine2: TracerEngine = mockk(relaxed = true)
    private val tracer = TracerImpl(listOf(mockEngine1, mockEngine2), emptyList())

    @Test
    fun `should delegate setOperationName`() {
        tracer.setOperationName("operationName")

        verify(exactly = 1) { mockEngine1.setOperationName("operationName") }
        verify(exactly = 1) { mockEngine2.setOperationName("operationName") }
    }

    @Test
    fun `should delegate addProperty for string`() {
        tracer.addProperty("key", "value")

        verify(exactly = 1) { mockEngine1.addProperty("key", "value") }
        verify(exactly = 1) { mockEngine2.addProperty("key", "value") }
    }

    @Test
    fun `should delegate addProperty for number`() {
        tracer.addProperty("key", 10)

        verify(exactly = 1) { mockEngine1.addProperty("key", 10) }
        verify(exactly = 1) { mockEngine2.addProperty("key", 10) }
    }

    @Test
    fun `should delegate addProperty for boolean`() {
        tracer.addProperty("key", true)

        verify(exactly = 1) { mockEngine1.addProperty("key", true) }
        verify(exactly = 1) { mockEngine2.addProperty("key", true) }
    }

    @Test
    fun `should delegate addProperty as String for List`() {
        val item: List<Int> = listOf(1, 2, 3, 4, 5)
        tracer.addProperty("key", item)

        verify(exactly = 1) { mockEngine1.addProperty("key", any<List<Any>>()) }
        verify(exactly = 1) { mockEngine2.addProperty("key", any<List<Any>>()) }
    }

    @Test
    fun `should delegate addRootProperty for string`() {
        tracer.addRootProperty("key", "value")

        verify(exactly = 1) { mockEngine1.addRootProperty("key", "value") }
        verify(exactly = 1) { mockEngine2.addRootProperty("key", "value") }
    }

    @Test
    fun `should delegate addRootProperty for number`() {
        tracer.addRootProperty("key", 10)

        verify(exactly = 1) { mockEngine1.addRootProperty("key", 10) }
        verify(exactly = 1) { mockEngine2.addRootProperty("key", 10) }
    }

    @Test
    fun `should delegate addRootProperty for boolean`() {
        tracer.addRootProperty("key", true)

        verify(exactly = 1) { mockEngine1.addRootProperty("key", true) }
        verify(exactly = 1) { mockEngine2.addRootProperty("key", true) }
    }

    @Test
    fun `should delegate recordExecutionTime`() {
        tracer.recordExecutionTime("execution") { context ->
            Thread.sleep(10)
            context["SomeKey"] = ":D"
            "Batata"
        }

        val expectedContext = mapOf("SomeKey" to ":D")
        verify(exactly = 1) { mockEngine1.recordExecutionTime("execution", match { it >= 10 }, expectedContext) }
        verify(exactly = 1) { mockEngine2.recordExecutionTime("execution", match { it >= 10 }, expectedContext) }
    }

    @Test
    fun `should delegate noticeError`() {
        val exception = RuntimeException(":]")
        tracer.notifyError(exception, false)

        verify(exactly = 1) { mockEngine1.notifyError(exception, false) }
        verify(exactly = 1) { mockEngine2.notifyError(exception, false) }
    }

    @Test
    fun `should delegate noticeError with message`() {
        tracer.notifyError("error", emptyMap(), false)

        verify(exactly = 1) { mockEngine1.notifyError("error", emptyMap(), false) }
        verify(exactly = 1) { mockEngine2.notifyError("error", emptyMap(), false) }
    }

    @Test
    fun `should delegate notifyRootError`() {
        val exception = RuntimeException(":]")
        tracer.notifyRootError(exception, false)

        verify(exactly = 1) { mockEngine1.notifyRootError(exception, false) }
        verify(exactly = 1) { mockEngine2.notifyRootError(exception, false) }
    }

    @Test
    fun `should delegate notifyRootError with message`() {
        tracer.notifyRootError("error", emptyMap(), false)

        verify(exactly = 1) { mockEngine1.notifyRootError("error", emptyMap(), false) }
        verify(exactly = 1) { mockEngine2.notifyRootError("error", emptyMap(), false) }
    }

    @Test
    fun `should delegate clear`() {
        tracer.clear()

        verify(exactly = 1) { mockEngine1.clear() }
        verify(exactly = 1) { mockEngine2.clear() }
    }
}
