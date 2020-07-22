package br.com.guiabolso.tracing.example

import br.com.guiabolso.tracing.engine.datadog.DatadogTracer
import br.com.guiabolso.tracing.factory.TracerFactory
import br.com.guiabolso.tracing.utils.DatadogUtils
import datadog.trace.api.Trace
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(DatadogTracer::class.java)

/**
 * See how to configure the agent at https://docs.datadoghq.com/tracing/setup/java/
 *
 * Start the JVM with '-javaagent:/path/to/the/dd-java-agent.jar'
 * Set environment variable 'DD_SERVICE_NAME'
 */
fun main(vararg args: String) {
    val tracer = TracerFactory.createTracerWithDatadog()
    val executor = Executors.newFixedThreadPool(2)

    // You don't need to use DatadogUtils.traceAsNewOperation when using servlet, its automatic.
    DatadogUtils.traceAsNewOperation("simpleOperation") {
        tracer.addProperty("oneTag", "someValue1")
        someWork()
    }

    DatadogUtils.traceAsNewOperation("simpleOperationWithError") {
        tracer.addProperty("oneTag", "someValue2")
        someWorkWithError()
    }

    DatadogUtils.traceAsNewOperation("simpleOperationWithErrorHandled") {
        tracer.addProperty("oneTag", "someValue3")
        try {
            someWorkWithError()
        } catch (e: Exception) {
            // Fallback
            // In this scenario only the child span will be marked as error
        }
    }

    DatadogUtils.traceAsNewOperation("someAsyncOperation") {
        tracer.addProperty("oneTag", "someValue4")
        someWork()

        tracer.executeAsync(executor) { someWork() }
        Thread.sleep(10)
        tracer.executeAsync(executor) { someWork() }
        Thread.sleep(10)
        tracer.executeAsync(executor) { someWorkWithError() }

        someWork()
    }

    DatadogUtils.traceAsNewOperation("someAsyncOperationWithError") {
        tracer.addProperty("oneTag", "someValue5")
        someWork()

        tracer.executeAsync(executor) { someWork() }
        Thread.sleep(10)
        tracer.executeAsync(executor) { someWork() }
        Thread.sleep(10)
        tracer.executeAsync(executor) { someWork() }
        Thread.sleep(10)

        someWorkWithError()
    }

    executor.shutdown()
    executor.awaitTermination(10, SECONDS)
}

// If you want to change the name of this method span set 'operationName'.
// Remember that unlike NewRelic's metrics '/' should not be used.
@Trace
private fun someWork() {
    logger.info("Starting some work")
    Thread.sleep(random(100, 200))
    logger.info("Finished some work")
}

@Trace
private fun someWorkWithError() {
    logger.info("Starting some work")
    Thread.sleep(random(100, 200))
    throw RuntimeException("This is an error.")
}

private fun random(min: Int, max: Int): Long = min.toLong() + (max * Math.random()).toLong()
