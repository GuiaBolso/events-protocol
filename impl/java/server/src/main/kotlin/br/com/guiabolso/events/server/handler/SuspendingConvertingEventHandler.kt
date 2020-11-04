package br.com.guiabolso.events.server.handler

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.model.ResponseEvent

interface SuspendingConvertingEventHandler<T> : SuspendingEventHandler {
    suspend fun convert(input: RequestEvent): T

    suspend fun handle(input: RequestEvent, converted: T): ResponseEvent

    override suspend fun coHandle(event: RequestEvent): ResponseEvent {
        return handle(event, convert(event))
    }
}
