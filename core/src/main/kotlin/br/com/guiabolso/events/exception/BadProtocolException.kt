package br.com.guiabolso.events.exception

class BadProtocolException(val payload: String, cause: Throwable) : RuntimeException(cause)