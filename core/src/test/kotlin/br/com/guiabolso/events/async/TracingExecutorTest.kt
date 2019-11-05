package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.engine.TracerEngine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TracingExecutorTest {

    @Test
    fun testContextForward() {
        val tracer: Tracer = mock()
        val tracerEngine: TracerEngine<*> = mock()
        val mockContext: Any = mock()
        val executor = TracingExecutorServiceWrapper(Executors.newSingleThreadExecutor(), tracer)

        whenever(tracer.getTracerEngine()).thenReturn(tracerEngine)
        whenever(tracerEngine.extractContext()).thenReturn(mockContext)

        val ret0 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertNull(ret0.get()?.id)
        assertNull(ret0.get()?.flowId)
        assertNull(ret0.get()?.origin)

        verify(tracerEngine, times(1)).extractContext()
        verify(tracerEngine, times(1)).withContext(mockContext)

        EventContextHolder.setContext(EventContext("id", "flowId", "unknown"))

        val ret1 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertEquals("id", ret1.get().id)
        assertEquals("flowId", ret1.get().flowId)
        assertEquals("unknown", ret1.get().origin)

        verify(tracerEngine, times(2)).extractContext()
        verify(tracerEngine, times(2)).withContext(mockContext)

        EventContextHolder.clean()

        val ret2 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertNull(ret2.get()?.id)
        assertNull(ret2.get()?.flowId)
        assertNull(ret2.get()?.origin)

        verify(tracerEngine, times(3)).extractContext()
        verify(tracerEngine, times(3)).withContext(mockContext)

        executor.shutdown()
    }

}