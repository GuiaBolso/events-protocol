package br.com.guiabolso.events.server

import br.com.guiabolso.events.EventBuilderForTest.buildRequestEvent
import br.com.guiabolso.events.EventBuilderForTest.buildResponseEvent
import br.com.guiabolso.events.json.MapperHolder.mapper
import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.exception.ExceptionHandlerRegistry
import br.com.guiabolso.events.server.handler.SimpleEventHandlerRegistry
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.system.measureNanoTime

class EventProcessorTest {

    private lateinit var rawEventProcessor: EventProcessor
    private lateinit var eventHandlerRegistry: SimpleEventHandlerRegistry
    private lateinit var exceptionHandlerRegistry: ExceptionHandlerRegistry

    @BeforeEach
    fun setUp() {
        eventHandlerRegistry = SimpleEventHandlerRegistry()
        exceptionHandlerRegistry = ExceptionHandlerRegistry()
        rawEventProcessor = EventProcessor(eventHandlerRegistry, exceptionHandlerRegistry)
    }

    @Test
    fun testCanProcessEvent() {
        val event = buildRequestEvent()

        eventHandlerRegistry.add(event.name, event.version) {
            buildResponseEvent()
        }

        val responseEvent = rawEventProcessor.processEvent(buildRequestEventString())

        assertEquals(
            "{\"name\":\"event:name:response\",\"version\":1,\"id\":\"id\",\"flowId\":\"flowId\",\"payload\":42,\"identity\":{},\"auth\":{},\"metadata\":{}}",
            responseEvent
        )
    }

    @Test
    fun testBadProtocolEventIsReturned() {
        val responseEvent =
            mapper.decodeFromString<RawEvent>(rawEventProcessor.processEvent("THIS IS NOT A EVENT"))

        assertEquals("badProtocol", responseEvent.name)
        assertEquals(
            "INVALID_COMMUNICATION_PROTOCOL",
            responseEvent.payload!!.jsonObject["code"]!!.jsonPrimitive.content
        )
    }

    @Test
    fun testBenchMark() {
        val utsEvent = this::class.java.getResourceAsStream("/test.json").bufferedReader().use {
            it.readText()
        }

        eventHandlerRegistry.add("test", 1) { evt ->
            evt.payloadAs<Input>()
            buildResponseEvent()
        }

        repeat(1000) {
            rawEventProcessor.processEvent(utsEvent)
        }

        val limit = 100_000
        val totalTime = (1..limit).fold(0L) { acc, _ ->
            val time = measureNanoTime {
                rawEventProcessor.processEvent(utsEvent)
            }
            acc + time
        }

        println("Total time: $totalTime")
        println("Took avg ms: ${totalTime / (limit.toDouble() * (1000 * 1000))}")
    }

    private fun buildRequestEventString(event: RequestEvent = buildRequestEvent()) = mapper.encodeToString(event)
}


@Serializable
data class Input(val variables: List<InputVariable>)

@Serializable
data class InputVariable(
    val key: String,
    val value: JsonPrimitive,
    val type: VariableType
)

@Serializable
enum class VariableType {
    INT32, INT64, FLOAT32, FLOAT64, STRING, BOOL
}
