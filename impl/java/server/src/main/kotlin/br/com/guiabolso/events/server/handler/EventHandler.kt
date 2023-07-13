package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.ResponseEvent

interface EventHandler {

    val eventName: String

    val eventVersion: Int

    suspend fun handle(event: RequestEventContext): ResponseEvent
}
