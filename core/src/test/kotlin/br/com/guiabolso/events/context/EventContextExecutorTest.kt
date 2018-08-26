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

        val ret0 = executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        assertNull(ret0.get().first)
        assertNull(ret0.get().second)

        EventContextHolder.setContext(EventContext("id", "flowId"))

        val ret1 = executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        assertEquals("id", ret1.get().first)
        assertEquals("flowId", ret1.get().second)

        EventContextHolder.clean()

        val ret2 = executor.submit(Callable<Pair<String?, String?>> { EventContextHolder.getContext()?.id to EventContextHolder.getContext()?.flowId })
        assertNull(ret2.get().first)
        assertNull(ret2.get().second)

        executor.shutdown()
    }
}