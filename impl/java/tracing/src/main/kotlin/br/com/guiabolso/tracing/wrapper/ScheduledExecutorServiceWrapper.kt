package br.com.guiabolso.tracing.wrapper

import br.com.guiabolso.tracing.context.ThreadContextManager
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ScheduledExecutorServiceWrapper(
    private val contextManagers: List<ThreadContextManager<*>>,
    private val delegate: ScheduledExecutorService
) : ExecutorServiceWrapper(contextManagers, delegate), ScheduledExecutorService {

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
