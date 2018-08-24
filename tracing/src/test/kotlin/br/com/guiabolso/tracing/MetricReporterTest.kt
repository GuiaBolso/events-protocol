package br.com.guiabolso.tracing

import br.com.guiabolso.tracing.factory.MetricReporterFactory
import br.com.guiabolso.tracing.utils.DatadogUtils
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main(vararg args: String) {
    val reporter = MetricReporterFactory.createDatadogMetricReporter()
    val executor = Executors.newFixedThreadPool(2)

    (1..100).forEach {
        DatadogUtils.traceOperation("testAsync1") {
            reporter.executeAsync(executor) {
                Thread.sleep(100L)
            }
            reporter.executeAsync(executor) {
                Thread.sleep(100L)
            }
            Thread.sleep(10L)
        }

        DatadogUtils.traceOperation("testAsync2") {
            reporter.executeAsync(executor) {
                Thread.sleep(100L)
            }
            reporter.executeAsync(executor) {
                Thread.sleep(100L)
            }
            Thread.sleep(200L)
        }
        Thread.sleep(it * 100L)
    }

    executor.shutdown()
    executor.awaitTermination(3, TimeUnit.MINUTES)
    Thread.sleep(20000L)
}