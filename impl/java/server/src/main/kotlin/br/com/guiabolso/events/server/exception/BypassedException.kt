package br.com.guiabolso.events.server.exception

import br.com.guiabolso.events.model.RequestEvent

data class BypassedException(val exception: Exception, val request: RequestEvent) : Exception()
