package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.ThreadContextManager
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ScheduledExecutorServiceWrapper(
    private val contextManagers: List<ThreadContextManager<*>>,
    private val delegate: ScheduledExecutorService
) : ScheduledExecutorService {

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

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return delegate.schedule(RunnableWrapper(contextManagers, command), delay, unit)
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        return delegate.schedule(CallableWrapper(contextManagers, callable), delay, unit)
    }

    override fun scheduleAtFixedRate(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<*> {
        return delegate.scheduleAtFixedRate(RunnableWrapper(contextManagers, command), initialDelay, period, unit)
    }

    override fun scheduleWithFixedDelay(command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return delegate.scheduleWithFixedDelay(RunnableWrapper(contextManagers, command), initialDelay, delay, unit)
    }
}
