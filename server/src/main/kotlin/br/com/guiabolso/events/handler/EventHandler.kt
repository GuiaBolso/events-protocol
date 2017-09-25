package br.com.guiabolso.events.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface EventHandler {

    fun handle(event: RequestEvent): ResponseEvent

}