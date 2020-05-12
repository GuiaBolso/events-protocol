package br.com.guiabolso.events.test

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.events.server.handler.ConvertingEventHandler
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny

fun <T> ConvertingEventHandler<T>.shouldConvert(inputEvent: RequestEvent) = shouldNotThrowAny { convert(inputEvent) }
fun ConvertingEventHandler<*>.shouldNotConvert(inputEvent: RequestEvent) = shouldThrowAny { convert(inputEvent) }
