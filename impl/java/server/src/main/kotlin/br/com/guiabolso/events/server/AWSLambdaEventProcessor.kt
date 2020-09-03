package br.com.guiabolso.events.server

import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.tracer.DefaultTracer
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import java.io.InputStream
import java.io.OutputStream
import org.slf4j.LoggerFactory

class AWSLambdaEventProcessor
@JvmOverloads
constructor(
    discovery: EventHandlerDiscovery,
    exceptionHandlerRegistry: ExceptionHandlerRegistry,
    tracer: Tracer = DefaultTracer,
    eventValidator: EventValidator = StrictEventValidator()
) {

    private val eventProcessor = EventProcessor(discovery, exceptionHandlerRegistry, tracer, eventValidator)

    fun processEvent(input: InputStream, output: OutputStream) {
        val payload = readInput(input)
        logger.debug("Input: $payload")
        val response = eventProcessor.processEvent(payload)
        logger.debug("Output: $payload")
        writeOutput(output, response)
    }

    private fun readInput(input: InputStream): String {
        return input.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun writeOutput(output: OutputStream, response: String) {
        output.use { it.write(response.toByteArray()) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AWSLambdaEventProcessor::class.java)
    }
}
