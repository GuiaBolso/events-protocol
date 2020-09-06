package br.com.guiabolso.events.context

import br.com.guiabolso.events.context.EventThreadContextManager.withContext
import br.com.guiabolso.tracing.builder.TracerBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class EventThreadContextManagerTest {

    private val tracer = TracerBuilder()
        .withSlf4J()
        .withContextManager(EventThreadContextManager)
        .build()

    @Test
    fun testContextForward() {
        val executor = tracer.wrap(Executors.newSingleThreadExecutor())

        withContext(EventContext(null, null)).use {
            val ret = executor.submit(Callable { EventThreadContextManager.current })
            assertNull(ret.get().id)
            assertNull(ret.get().flowId)
        }

        withContext(EventContext("id", "flowId")).use {
            val ret = executor.submit(Callable { EventThreadContextManager.current })
            assertEquals("id", ret.get().id)
            assertEquals("flowId", ret.get().flowId)
        }

        val ret = executor.submit(Callable { EventThreadContextManager.current })
        assertNull(ret.get().id)
        assertNull(ret.get().flowId)

        executor.shutdown()
    }
}
