package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface ConvertingEventHandler<T> : EventHandler {
    fun convert(input: RequestEvent): T

    suspend fun handle(input: RequestEvent, converted: T): ResponseEvent

    override suspend fun handle(event: RequestEvent): ResponseEvent = handle(event, convert(event))
}
