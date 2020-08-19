package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.engine.TracerEngine
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.Closeable
import org.junit.jupiter.api.Test

class CompositeTracerEngineTest {

    private val mockEngine1: TracerEngine<*> = mockk(relaxed = true)
    private val mockEngine2: TracerEngine<*> = mockk(relaxed = true)
    private val engine = CompositeTracerEngine(listOf(mockEngine1, mockEngine2))

    @Test
    fun `should delegate setOperationName`() {
        engine.setOperationName("operationName")

        verify(exactly = 1) { mockEngine1.setOperationName("operationName") }
        verify(exactly = 1) { mockEngine2.setOperationName("operationName") }
    }

    @Test
    fun `should delegate addProperty for string`() {
        engine.addProperty("key", "value")

        verify(exactly = 1) { mockEngine1.addProperty("key", "value") }
        verify(exactly = 1) { mockEngine2.addProperty("key", "value") }
    }

    @Test
    fun `should delegate addProperty for number`() {
        engine.addProperty("key", 10)

        verify(exactly = 1) { mockEngine1.addProperty("key", 10) }
        verify(exactly = 1) { mockEngine2.addProperty("key", 10) }
    }

    @Test
    fun `should delegate addProperty for boolean`() {
        engine.addProperty("key", true)

        verify(exactly = 1) { mockEngine1.addProperty("key", true) }
        verify(exactly = 1) { mockEngine2.addProperty("key", true) }
    }

    @Test
    fun `should delegate addProperty as String for List`() {
        val item: List<Int> = listOf(1, 2, 3, 4, 5)
        engine.addProperty("key", item)

        verify(exactly = 1) { mockEngine1.addProperty("key", any<List<Any>>()) }
        verify(exactly = 1) { mockEngine2.addProperty("key", any<List<Any>>()) }
    }

    @Test
    fun `should delegate noticeError`() {
        val exception = RuntimeException(":]")
        engine.notifyError(exception, false)

        verify(exactly = 1) { mockEngine1.notifyError(exception, false) }
        verify(exactly = 1) { mockEngine2.notifyError(exception, false) }
    }

    @Test
    fun `should delegate noticeError with message`() {
        engine.notifyError("error", emptyMap(), false)

        verify(exactly = 1) { mockEngine1.notifyError("error", emptyMap(), false) }
        verify(exactly = 1) { mockEngine2.notifyError("error", emptyMap(), false) }
    }

    @Test
    fun `should delegate extractContext`() {
        every { mockEngine1.extractContext() } returns "context"
        every { mockEngine2.extractContext() } returns "context"

        engine.extractContext()

        verify(exactly = 1) { mockEngine1.extractContext() }
        verify(exactly = 1) { mockEngine2.extractContext() }
    }

    @Test
    fun `should delegate withContext closeable`() {
        every { mockEngine1.extractContext() } returns "context"
        every { mockEngine2.extractContext() } returns "context"

        val closeable1: Closeable = mockk()
        val closeable2: Closeable = mockk()
        every { mockEngine1.withContext("context") } returns closeable1
        every { mockEngine2.withContext("context") } returns closeable2

        engine.withContext(engine.extractContext()).close()

        verify(exactly = 1) { mockEngine1.withContext("context") }
        verify(exactly = 1) { mockEngine2.withContext("context") }
        verify(exactly = 1) { closeable1.close() }
        verify(exactly = 1) { closeable2.close() }
    }

    @Test
    fun `should delegate withContext`() {
        every { mockEngine1.extractContext() } returns "context"
        every { mockEngine2.extractContext() } returns "context"

        val closeable1: Closeable = mockk()
        val closeable2: Closeable = mockk()
        every { mockEngine1.withContext("context") } returns closeable1
        every { mockEngine2.withContext("context") } returns closeable2

        engine.withContext(engine.extractContext()) {
        }

        verify(exactly = 1) { mockEngine1.withContext("context") }
        verify(exactly = 1) { mockEngine2.withContext("context") }
        verify(exactly = 1) { closeable1.close() }
        verify(exactly = 1) { closeable2.close() }
    }

    @Test
    fun `should delegate clear`() {
        engine.clear()

        verify(exactly = 1) { mockEngine1.clear() }
        verify(exactly = 1) { mockEngine2.clear() }
    }
}
