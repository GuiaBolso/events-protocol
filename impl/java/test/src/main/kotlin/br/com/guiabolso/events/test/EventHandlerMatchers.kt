package br.com.guiabolso.events.test

import br.com.guiabolso.events.server.handler.ConvertingEventHandler
import br.com.guiabolso.events.server.handler.RequestEventContext
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny

fun <T> ConvertingEventHandler<T>.shouldConvert(inputEvent: RequestEventContext) = shouldNotThrowAny { convert(inputEvent) }
fun ConvertingEventHandler<*>.shouldNotConvert(inputEvent: RequestEventContext) = shouldThrowAny { convert(inputEvent) }
