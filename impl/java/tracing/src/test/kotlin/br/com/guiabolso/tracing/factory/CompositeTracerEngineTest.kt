package br.com.guiabolso.tracing.factory

import br.com.guiabolso.tracing.engine.TracerEngine
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import java.io.Closeable
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList

class CompositeTracerEngineTest {

    private val mockEngine1: TracerEngine<*> = mock()
    private val mockEngine2: TracerEngine<*> = mock()
    private val engine = CompositeTracerEngine(listOf(mockEngine1, mockEngine2))

    @Test
    fun `should delegate setOperationName`() {
        engine.setOperationName("operationName")

        verify(mockEngine1, times(1)).setOperationName("operationName")
        verify(mockEngine2, times(1)).setOperationName("operationName")
    }

    @Test
    fun `should delegate addProperty for string`() {
        engine.addProperty("key", "value")

        verify(mockEngine1, times(1)).addProperty("key", "value")
        verify(mockEngine2, times(1)).addProperty("key", "value")
    }

    @Test
    fun `should delegate addProperty for number`() {
        engine.addProperty("key", 10)

        verify(mockEngine1, times(1)).addProperty("key", 10)
        verify(mockEngine2, times(1)).addProperty("key", 10)
    }

    @Test
    fun `should delegate addProperty for boolean`() {
        engine.addProperty("key", true)

        verify(mockEngine1, times(1)).addProperty("key", true)
        verify(mockEngine2, times(1)).addProperty("key", true)
    }

    @Test
    fun `should delegate addProperty as String for List`() {
        val item: List<Int> = listOf(1, 2, 3, 4, 5)
        engine.addProperty("key", item)

        verify(mockEngine1, times(1)).addProperty(eq("key"), anyList<Int>())
        verify(mockEngine2, times(1)).addProperty(eq("key"), anyList<Int>())
    }

    @Test
    fun `should delegate noticeError`() {
        val exception = RuntimeException(":]")
        engine.notifyError(exception, false)

        verify(mockEngine1, times(1)).notifyError(exception, false)
        verify(mockEngine2, times(1)).notifyError(exception, false)
    }

    @Test
    fun `should delegate noticeError with message`() {
        engine.notifyError("error", emptyMap(), false)

        verify(mockEngine1, times(1)).notifyError("error", emptyMap(), false)
        verify(mockEngine2, times(1)).notifyError("error", emptyMap(), false)
    }

    @Test
    fun `should delegate extractContext`() {
        whenever(mockEngine1.extractContext()).thenReturn("context")
        whenever(mockEngine2.extractContext()).thenReturn("context")

        engine.extractContext()

        verify(mockEngine1, times(1)).extractContext()
        verify(mockEngine2, times(1)).extractContext()
    }

    @Test
    fun `should delegate withContext closeable`() {
        whenever(mockEngine1.extractContext()).thenReturn("context")
        whenever(mockEngine2.extractContext()).thenReturn("context")

        val closeable1: Closeable = mock()
        val closeable2: Closeable = mock()
        whenever(mockEngine1.withContext("context")).thenReturn(closeable1)
        whenever(mockEngine2.withContext("context")).thenReturn(closeable2)

        engine.withContext(engine.extractContext()).close()

        verify(mockEngine1, times(1)).withContext("context")
        verify(mockEngine2, times(1)).withContext("context")
        verify(closeable1, times(1)).close()
        verify(closeable2, times(1)).close()
    }

    @Test
    fun `should delegate withContext`() {
        whenever(mockEngine1.extractContext()).thenReturn("context")
        whenever(mockEngine2.extractContext()).thenReturn("context")

        val closeable1: Closeable = mock()
        val closeable2: Closeable = mock()
        whenever(mockEngine1.withContext("context")).thenReturn(closeable1)
        whenever(mockEngine2.withContext("context")).thenReturn(closeable2)

        engine.withContext(engine.extractContext()) {
        }

        verify(mockEngine1, times(1)).withContext("context")
        verify(mockEngine2, times(1)).withContext("context")
        verify(closeable1, times(1)).close()
        verify(closeable2, times(1)).close()
    }

    @Test
    fun `should delegate clear`() {
        engine.clear()

        verify(mockEngine1, times(1)).clear()
        verify(mockEngine2, times(1)).clear()
    }
}
