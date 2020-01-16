package br.com.guiabolso.events.async

import br.com.guiabolso.events.context.EventContextHolder
import br.com.guiabolso.tracing.Tracer
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class TracingExecutorServiceWrapper(
    private val executorService: ExecutorService,
    private val tracer: Tracer
) : ExecutorService {

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        val engine = tracer.getTracerEngine()
        return executorService.submit(
            TracingCallable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                callable = task
            )
        )
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        val engine = tracer.getTracerEngine()
        return executorService.submit(
            TracingRunnable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                runnable = task
            ), result
        )
    }

    override fun submit(task: Runnable): Future<*> {
        val engine = tracer.getTracerEngine()
        return executorService.submit(
            TracingRunnable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                runnable = task
            )
        )
    }

    override fun execute(command: Runnable) {
        val engine = tracer.getTracerEngine()
        return executorService.execute(
            TracingRunnable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                runnable = command
            )
        )
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
        val engine = tracer.getTracerEngine()
        return executorService.invokeAny(tasks.map {
            TracingCallable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                callable = it
            )
        })
    }

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        val engine = tracer.getTracerEngine()
        return executorService.invokeAny(
            tasks.map {
                TracingCallable(
                    tracerEngine = engine,
                    tracingContext = engine.extractContext()!!,
                    eventContext = EventContextHolder.getContext(),
                    callable = it
                )
            },
            timeout,
            unit
        )
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        val engine = tracer.getTracerEngine()
        return executorService.invokeAll(tasks.map {
            TracingCallable(
                tracerEngine = engine,
                tracingContext = engine.extractContext()!!,
                eventContext = EventContextHolder.getContext(),
                callable = it
            )
        })
    }

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> {
        val engine = tracer.getTracerEngine()
        return executorService.invokeAll(
            tasks.map {
                TracingCallable(
                    tracerEngine = engine,
                    tracingContext = engine.extractContext()!!,
                    eventContext = EventContextHolder.getContext(),
                    callable = it
                )
            },
            timeout,
            unit
        )
    }

}