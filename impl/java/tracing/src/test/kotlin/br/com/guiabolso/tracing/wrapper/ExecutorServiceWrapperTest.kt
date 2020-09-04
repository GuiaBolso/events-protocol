package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.SimpleThreadContextManager
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecutorServiceWrapperTest {

    @Test
    fun `should execute simples delegation when no task is involved`() {
        val executor = mockk<ExecutorService>()
        val wrapper = ExecutorServiceWrapper(emptyList(), executor)

        every { executor.shutdown() } just Runs
        wrapper.shutdown()
        verify { executor.shutdown() }

        every { executor.shutdownNow() } returns emptyList()
        wrapper.shutdownNow()
        verify { executor.shutdownNow() }

        every { executor.isShutdown } returns false
        wrapper.isShutdown
        verify { executor.isShutdown }

        every { executor.isTerminated } returns false
        wrapper.isTerminated
        verify { executor.isTerminated }

        every { executor.awaitTermination(1, TimeUnit.SECONDS) } returns false
        wrapper.awaitTermination(1, TimeUnit.SECONDS)
        verify { executor.awaitTermination(1, TimeUnit.SECONDS) }
    }

    @Test
    fun `should submit a new Callable with a given context`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        var context: String?
        context = wrapper.submit(Callable { contextManager.extract() }).get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.submit(Callable { contextManager.extract() }).get()
        }
        assertEquals("SomeContext", context)

        context = wrapper.submit(Callable { contextManager.extract() }).get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should submit a new Runnable and result with a given context`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        var context: String? = null
        assertEquals("Batata", wrapper.submit({ context = contextManager.extract() }, "Batata").get())
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            assertEquals("Batata", wrapper.submit({ context = contextManager.extract() }, "Batata").get())
        }
        assertEquals("SomeContext", context)

        assertEquals("Batata", wrapper.submit({ context = contextManager.extract() }, "Batata").get())
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should submit a new Runnable with a given context`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        var context: String? = null
        wrapper.submit { context = contextManager.extract() }.get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            wrapper.submit { context = contextManager.extract() }.get()
        }
        assertEquals("SomeContext", context)

        wrapper.submit { context = contextManager.extract() }.get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should invokeAll with a given context`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        val task: Callable<String> = Callable<String> { contextManager.extract() }

        var context: String?
        context = wrapper.invokeAll(mutableListOf(task)).first().get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.invokeAll(mutableListOf(task)).first().get()
        }
        assertEquals("SomeContext", context)

        context = wrapper.invokeAll(mutableListOf(task)).first().get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should invokeAll with a given context and timeout`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        val task: Callable<String> = Callable<String> { contextManager.extract() }

        var context: String?
        context = wrapper.invokeAll(mutableListOf(task), 1, TimeUnit.SECONDS).first().get()
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.invokeAll(mutableListOf(task), 1, TimeUnit.SECONDS).first().get()
        }
        assertEquals("SomeContext", context)

        context = wrapper.invokeAll(mutableListOf(task), 1, TimeUnit.SECONDS).first().get()
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should invokeAny with a given context`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        val task: Callable<String> = Callable<String> { contextManager.extract() }

        var context: String?
        context = wrapper.invokeAny(mutableListOf(task))
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.invokeAny(mutableListOf(task))
        }
        assertEquals("SomeContext", context)

        context = wrapper.invokeAny(mutableListOf(task))
        assertEquals("None", context)

        executor.shutdown()
    }

    @Test
    fun `should invokeAny with a given context and timeout`() {
        val executor = Executors.newSingleThreadExecutor()
        val contextManager = SimpleThreadContextManager()
        val wrapper = ExecutorServiceWrapper(listOf(contextManager), executor)

        val task: Callable<String> = Callable<String> { contextManager.extract() }

        var context: String?
        context = wrapper.invokeAny(mutableListOf(task), 1, TimeUnit.SECONDS)
        assertEquals("None", context)

        contextManager.withContext("SomeContext").use {
            context = wrapper.invokeAny(mutableListOf(task), 1, TimeUnit.SECONDS)
        }
        assertEquals("SomeContext", context)

        context = wrapper.invokeAny(mutableListOf(task), 1, TimeUnit.SECONDS)
        assertEquals("None", context)

        executor.shutdown()
    }
}
