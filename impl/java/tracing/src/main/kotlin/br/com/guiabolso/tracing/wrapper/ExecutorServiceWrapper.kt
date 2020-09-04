package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.ThreadContextManager
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

open class ExecutorServiceWrapper(
    private val contextManagers: List<ThreadContextManager<*>>,
    private val delegate: ExecutorService
) : ExecutorService {

    override fun execute(command: Runnable) {
        delegate.execute(RunnableWrapper(contextManagers, command))
    }

    override fun shutdown() {
        delegate.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        return delegate.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        return delegate.isShutdown
    }

    override fun isTerminated(): Boolean {
        return delegate.isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return delegate.awaitTermination(timeout, unit)
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return delegate.submit(CallableWrapper(contextManagers, task))
    }

    override fun <T> submit(task: Runnable, result: T): Future<T> {
        return delegate.submit(RunnableWrapper(contextManagers, task), result)
    }

    override fun submit(task: Runnable): Future<*> {
        return delegate.submit(RunnableWrapper(contextManagers, task))
    }

    override fun <T> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { CallableWrapper(contextManagers, it) })
    }

    override fun <T> invokeAll(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): MutableList<Future<T>> {
        return delegate.invokeAll(tasks.map { CallableWrapper(contextManagers, it) }, timeout, unit)
    }

    override fun <T> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return delegate.invokeAny(tasks.map { CallableWrapper(contextManagers, it) })
    }

    override fun <T> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return delegate.invokeAny(tasks.map { CallableWrapper(contextManagers, it) }, timeout, unit)
    }
}
