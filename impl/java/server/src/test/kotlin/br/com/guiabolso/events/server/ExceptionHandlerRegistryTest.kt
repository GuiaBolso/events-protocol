package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest
import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.json.JsonAdapterProducer.mapper
import br.com.guiabolso.events.json.PrimitiveNode
import br.com.guiabolso.events.json.primitiveNode
import br.com.guiabolso.events.json.treeNode
import br.com.guiabolso.events.server.exception.handler.ExceptionHandlerRegistryFactory.exceptionHandler
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExceptionHandlerRegistryTest {
    private val builder = EventBuilder(mapper)

    @Test
    fun testCanRegisterExceptionHandler() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler(builder)

        exceptionHandlerRegistry.register(RuntimeException::class.java) { exception, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = mapper.toJsonTree(exception.message))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            requestEventContext(jsonAdapter = mapper),
            mockk(relaxed = true)
        )

        assertEquals("Some error", response.payload.primitiveNode.value)
    }

    @Test
    fun testExceptionPriority() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler(builder)

        exceptionHandlerRegistry.register(Exception::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = PrimitiveNode("Exception"))
        }

        exceptionHandlerRegistry.register(RuntimeException::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = PrimitiveNode("RuntimeException"))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            requestEventContext(jsonAdapter = mapper),
            mockk(relaxed = true)
        )

        assertEquals("Exception", response.payload.primitiveNode.value)
    }

    @Test
    fun testExceptionPriority2() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler(builder)

        exceptionHandlerRegistry.register(RuntimeException::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = PrimitiveNode("RuntimeException"))
        }

        exceptionHandlerRegistry.register(Exception::class.java) { _, _, _ ->
            EventBuilderForTest.buildResponseEvent().copy(payload = PrimitiveNode("Exception"))
        }

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            requestEventContext(jsonAdapter = mapper),
            mockk(relaxed = true)
        )

        assertEquals("RuntimeException", response.payload.primitiveNode.value)
    }

    @Test
    fun testHandleDefaultError() = runBlocking {
        val exceptionHandlerRegistry = exceptionHandler(builder)

        val response = exceptionHandlerRegistry.handleException(
            RuntimeException("Some error"),
            requestEventContext(jsonAdapter = mapper),
            mockk(relaxed = true)
        )

        assertEquals("UNHANDLED_ERROR", response.payload.treeNode["code"]!!.primitiveNode.value)
    }
}
