package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.validation.validateInput
import kotlin.reflect.KClass

abstract class InsecureTypedEventHandler<IN : Any, OUT : Any> : EventHandler {

    abstract val inputType: KClass<IN>
    abstract val outputType: KClass<OUT>

    override fun handle(event: RequestEvent): ResponseEvent {
        val payload = event.payloadAs(inputType.java)

        validateInput(payload)

        return responseFor(event) {
            this.payload = handle(payload)
        }
    }

    abstract fun handle(payload: IN): OUT

}