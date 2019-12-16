package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.engine.TracerEngine
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.jupiter.api.Assertions
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

        val ret0 =
            executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        Assertions.assertNull(ret0.get().first)
        Assertions.assertNull(ret0.get().second)

        EventContextHolder.setContext(EventContext("id", "flowId"))

        val ret1 =
            executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        Assertions.assertEquals("id", ret1.get().first)
        Assertions.assertEquals("flowId", ret1.get().second)

        EventContextHolder.clean()

        val ret2 =
            executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        Assertions.assertNull(ret2.get().first)
        Assertions.assertNull(ret2.get().second)

        executor.shutdown()
    }

}