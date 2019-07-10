package br.com.guiabolso.events.context

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class EventContextExecutorServiceWrapper(private val executorService: ExecutorService) : ExecutorService {

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        return executorService.submit(CallableWrapper(EventContextHolder.getContext(), task))
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return executorService.submit(RunnableWrapper(EventContextHolder.getContext(), task), result)
    }

    override fun submit(task: Runnable): Future<*> {
        return executorService.submit(RunnableWrapper(EventContextHolder.getContext(), task))
    }

    override fun execute(command: Runnable) {
        return executorService.execute(RunnableWrapper(EventContextHolder.getContext(), command))
    }

    override fun isShutdown(): Boolean {
        return executorService.isShutdown
    }

    override fun shutdown() {
        executorService.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        return executorService.shutdownNow()
    }

    override fun isTerminated(): Boolean {
        return executorService.isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return executorService.awaitTermination(timeout, unit)
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        return executorService.invokeAny(tasks.map { CallableWrapper(EventContextHolder.getContext(), it) })
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        return executorService.invokeAny(
            tasks.map { CallableWrapper(EventContextHolder.getContext(), it) },
            timeout,
            unit
        )
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        return executorService.invokeAll(tasks.map { CallableWrapper(EventContextHolder.getContext(), it) })
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        return executorService.invokeAll(
            tasks.map { CallableWrapper(EventContextHolder.getContext(), it) },
            timeout,
            unit
        )
    }

}