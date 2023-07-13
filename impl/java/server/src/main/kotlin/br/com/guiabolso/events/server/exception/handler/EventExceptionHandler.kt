package br.com.guiabolso.events.server.exception.handler

import br.com.guiabolso.events.model.ResponseEvent
import br.com.guiabolso.events.server.handler.RequestEventContext
import br.com.guiabolso.tracing.Tracer

interface EventExceptionHandler<in T : Throwable> {

    suspend fun handleException(exception: T, event: RequestEventContext, tracer: Tracer): ResponseEvent
}
