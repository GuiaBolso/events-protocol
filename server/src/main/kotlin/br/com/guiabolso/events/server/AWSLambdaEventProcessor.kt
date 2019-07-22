package br.com.guiabolso.events.server

import br.com.guiabolso.events.builder.EventBuilder.Companion.badProtocol
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.EventHandlerDiscovery
import br.com.guiabolso.events.server.parser.EventParsingException
import br.com.guiabolso.events.validation.EventValidator
import br.com.guiabolso.events.validation.StrictEventValidator
import br.com.guiabolso.tracing.Tracer
import br.com.guiabolso.tracing.factory.TracerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class AWSLambdaEventProcessor
@JvmOverloads
constructor(
    discovery: EventHandlerDiscovery,
    exceptionHandlerRegistry: ExceptionHandlerRegistry,
    private val tracer: Tracer = TracerFactory.createTracer(),
    eventValidator: EventValidator = StrictEventValidator()
) {

    private val eventProcessor = RawEventProcessor(discovery, exceptionHandlerRegistry, tracer, eventValidator)

    fun processEvent(input: InputStream, output: OutputStream) {
        val payload = readInput(input)

        val response = try {
            val rawEvent = parseEvent(payload)
            eventProcessor.processEvent(rawEvent)
        } catch (e: EventParsingException) {
            tracer.notifyError(e, false)
            badProtocol(e.eventMessage)
        }

        writeOutput(output, response.json())
    }

    private fun parseEvent(payload: String?): RawEvent? {
        try {
            val lambdaBody = MapperHolder.mapper.fromJson(payload, LambdaRequest::class.java)?.body ?: return null
            return MapperHolder.mapper.fromJson(lambdaBody, RawEvent::class.java)
        } catch (e: Throwable) {
            throw EventParsingException(e)
        }
    }

    private fun readInput(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        return reader.use { reader.readText() }
    }

    private fun writeOutput(output: OutputStream, response: String) {
        output.use { it.write(response.toByteArray()) }
    }

    private fun ResponseEvent.json() = MapperHolder.mapper.toJson(
        LambdaResponse(
            statusCode = 200,
            headers = mapOf("Content-Type" to "application/json"),
            body = MapperHolder.mapper.toJson(this)
        )
    )

    private data class LambdaRequest(
        val body: String?
    )

    private data class LambdaResponse(
        val statusCode: Int,
        val headers: Map<String, String>,
        val body: String
    )

}