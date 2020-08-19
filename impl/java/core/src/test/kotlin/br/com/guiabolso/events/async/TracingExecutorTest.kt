package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContext
import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.engine.TracerEngine
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.io.Closeable
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class TracingExecutorTest {

    @Test
    fun testContextForward() {
        val tracer: Tracer = mockk()
        val tracerEngine: TracerEngine<*> = mockk()
        val mockContext: Any = mockk()
        val executor = TracingExecutorServiceWrapper(Executors.newSingleThreadExecutor(), tracer)

        every { tracer.getTracerEngine() } returns tracerEngine
        every { tracerEngine.withContext(any()) } returns Closeable { }
        every { tracerEngine.extractContext() } returns mockContext

        val ret0 =
            executor.submit(Callable { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        assertNull(ret0.get().first)
        assertNull(ret0.get().second)

        EventContextHolder.setContext(EventContext("id", "flowId"))

        val ret1 =
            executor.submit(Callable { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        Assertions.assertEquals("id", ret1.get().first)
        Assertions.assertEquals("flowId", ret1.get().second)

        EventContextHolder.clean()

        val ret2 =
            executor.submit(Callable { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        assertNull(ret2.get().first)
        assertNull(ret2.get().second)

        executor.shutdown()
    }
}
