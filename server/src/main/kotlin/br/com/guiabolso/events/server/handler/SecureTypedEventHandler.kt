package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.builder.EventBuilder.Companion.responseFor
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.validation.required
import br.com.guiabolso.events.validation.validateInput
import kotlin.reflect.KClass

abstract class SecureTypedEventHandler<IN : Any, OUT : Any> : EventHandler {

    abstract val inputType: KClass<IN>
    abstract val outputType: KClass<OUT>

    override fun handle(event: RequestEvent): ResponseEvent {
        val userId = event.userId.required("userId")
        val payload = event.payloadAs(inputType.java)

        validateInput(payload)

        return responseFor(event) {
            this.payload = handle(userId, payload)
        }
    }

    abstract fun handle(userId: Long, payload: IN): OUT

}