package br.com.guiabolso.events.test

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.ConvertingEventHandler
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec

class EventHandlerMatchersTests : FunSpec({
    
    test("should convert") {
        MyHandler.shouldConvert(createEvent("success"))
        shouldThrowAny { MyHandler.shouldConvert(createEvent("error")) }
    }
    
    test("should not convert") {
        MyHandler.shouldNotConvert(createEvent("error"))
        shouldThrowAny { MyHandler.shouldNotConvert(createEvent("success")) }
    }
})

private fun createEvent(str: String) = EventBuilder.event {
    name = "a"
    version = 1
    id = "id"
    flowId = "flowId"
    payload = str
}

object MyHandler : ConvertingEventHandler<String> {
    override val eventName = "a"
    override val eventVersion = 1

    override fun convert(input: RequestEvent): String {
        val str = input.payloadAs<String>()
        if (str != "success")
            throw RuntimeException("Failure")
        return str
    }

    override fun handle(input: RequestEvent, converted: String): ResponseEvent { 
        throw NotImplementedError()
    }
}
