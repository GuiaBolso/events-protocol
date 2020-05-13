package br.com.guiabolso.events.client.exception

class BadProtocolException(val payload: String, cause: Throwable) : RuntimeException(cause)
