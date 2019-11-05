package br.com.guiabolso.events.context

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class EventContextExecutorTest {

    @Test
    fun testContextForward() {
        val executor = EventContextExecutorServiceWrapper(Executors.newSingleThreadExecutor())

        val ret0 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertNull(ret0.get()?.id)
        assertNull(ret0.get()?.flowId)
        assertNull(ret0.get()?.origin)

        EventContextHolder.setContext(EventContext("id", "flowId", "unknown"))

        val ret1 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertEquals("id", ret1.get().id)
        assertEquals("flowId", ret1.get().flowId)
        assertEquals("unknown", ret1.get().origin)

        EventContextHolder.clean()

        val ret2 = executor.submit(Callable<EventContext> { EventContextHolder.getContext() })
        assertNull(ret2.get()?.id)
        assertNull(ret2.get()?.flowId)
        assertNull(ret2.get()?.origin)

        executor.shutdown()
    }
}