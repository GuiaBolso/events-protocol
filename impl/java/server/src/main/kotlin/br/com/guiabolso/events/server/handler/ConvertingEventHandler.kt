package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.ResponseEvent

interface ConvertingEventHandler<T> : EventHandler {
    fun convert(input: RequestEventContext): T

    suspend fun handle(input: RequestEventContext, converted: T): ResponseEvent

    override suspend fun handle(event: RequestEventContext): ResponseEvent = handle(event, convert(event))
}
