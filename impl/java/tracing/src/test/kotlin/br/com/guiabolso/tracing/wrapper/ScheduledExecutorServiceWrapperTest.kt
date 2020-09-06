package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.SimpleThreadContextManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduledExecutorServiceWrapperTest {

    @Test
    fun `should schedule a new Runnable with a given context`() {
        val executor = Executors.newScheduledThreadPool(1)
        val contextManager = SimpleThreadContextManager()
        val wrapper = ScheduledExecutorServiceWrapper(listOf(contextManager), executor)

        var context: String? = null
        wrapper.schedule({ context = contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            wrapper.schedule({ context = contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        }
        assertEquals("SomeContext", context)

        wrapper.schedule({ context = contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should schedule a new Callable with a given context`() {
        val executor = Executors.newScheduledThreadPool(1)
        val contextManager = SimpleThreadContextManager()
        val wrapper = ScheduledExecutorServiceWrapper(listOf(contextManager), executor)

        var context: String?
        context = wrapper.schedule(Callable { contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.schedule(Callable { contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        }
        assertEquals("SomeContext", context)

        context = wrapper.schedule(Callable { contextManager.extract() }, 10, TimeUnit.MILLISECONDS).get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should schedule a new Callable at a fixed rate with a given context`() {
        val executor = Executors.newScheduledThreadPool(1)
        val contextManager = SimpleThreadContextManager()
        val wrapper = ScheduledExecutorServiceWrapper(listOf(contextManager), executor)

        var context0: String? = null
        var context1: String? = null
        var context2: String? = null

        wrapper.scheduleAtFixedRate({ context0 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)
        contextManager.withContext("SomeContext").use {
            wrapper.scheduleAtFixedRate({ context1 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)
        }
        wrapper.scheduleAtFixedRate({ context2 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)

        Thread.sleep(20)
        executor.shutdown()

        assertEquals("None", context0)
        assertEquals("SomeContext", context1)
        assertEquals("None", context2)
    }

    @Test
    fun `should schedule a new Callable at a fixed delay with a given context`() {
        val executor = Executors.newScheduledThreadPool(1)
        val contextManager = SimpleThreadContextManager()
        val wrapper = ScheduledExecutorServiceWrapper(listOf(contextManager), executor)

        var context0: String? = null
        var context1: String? = null
        var context2: String? = null

        wrapper.scheduleWithFixedDelay({ context0 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)
        contextManager.withContext("SomeContext").use {
            wrapper.scheduleWithFixedDelay({ context1 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)
        }
        wrapper.scheduleWithFixedDelay({ context2 = contextManager.extract() }, 0, 10, TimeUnit.MILLISECONDS)

        Thread.sleep(20)
        executor.shutdown()

        assertEquals("None", context0)
        assertEquals("SomeContext", context1)
        assertEquals("None", context2)
    }
}
