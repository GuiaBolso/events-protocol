package br.com.guiabolso.events.validation

import br.com.guiabolso.events.model.RawEvent
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface EventValidator {

    fun validateAsResponseEvent(rawEvent: RawEvent): ResponseEvent

    fun validateAsRequestEvent(rawEvent: RawEvent): RequestEvent

    fun <T> T?.required(name: String): T = this ?: throw IllegalArgumentException(name)

}