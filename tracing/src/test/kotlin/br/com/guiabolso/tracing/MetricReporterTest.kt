package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.factory.TracerFactory
import br.com.guiabolso.tracing.utils.DatadogUtils
import datadog.trace.api.Trace
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(vararg args: String) {
    val reporter = TracerFactory.createTracerWithDatadog()
    val executor = Executors.newFixedThreadPool(2)

    (1..100).forEach {
        DatadogUtils.traceOperation("testAsync4") {
            reporter.addProperty("run", it)
            reporter.executeAsync(executor) {
                Thread.sleep((100 * Math.random()).toLong())
            }
            reporter.executeAsync(executor) {
                teste(reporter)
            }
            Thread.sleep(150L)
        }
    }


    executor.shutdown()
    executor.awaitTermination(3, TimeUnit.MINUTES)
    Thread.sleep(20000L)
}

@Trace
fun teste(reporter: Tracer) {
    Thread.sleep((100 * Math.random()).toLong())
    throw RuntimeException(":[")
}