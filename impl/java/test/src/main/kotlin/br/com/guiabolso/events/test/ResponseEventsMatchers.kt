package br.com.guiabolso.events.test

import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.ResponseEvent
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun ResponseEvent.shouldBeSuccess() = this should beSuccess()
fun ResponseEvent.shouldNotBeSuccess() = this shouldNot beSuccess()
fun beSuccess() = object : Matcher<ResponseEvent> {
    override fun test(value: ResponseEvent) = MatcherResult(
        value.isSuccess(),
        "Event should be success, but isn't",
        "Event should not be success, but is"
    )
}

fun ResponseEvent.shouldBeError() = this should beError()
fun ResponseEvent.shouldNotBeError() = this shouldNot beError()
fun beError() = object : Matcher<ResponseEvent> {
    override fun test(value: ResponseEvent) = MatcherResult(
        value.isError(),
        "Event should be error, but isn't",
        "Event should not be error, but is"
    )
}

fun ResponseEvent.shouldBeRedirect() = this should beRedirect()
fun ResponseEvent.shouldNotBeRedirect() = this shouldNot beRedirect()
fun beRedirect() = object : Matcher<ResponseEvent> {
    override fun test(value: ResponseEvent) = MatcherResult(
        value.isRedirect(),
        "Event should be redirect, but isn't",
        "Event should not be redirect, but is"
    )
}

infix fun ResponseEvent.shouldHaveErrorType(errorType: EventErrorType) = this should haveErrorType(errorType)
infix fun ResponseEvent.shouldNotHaveErrorType(errorType: EventErrorType) = this shouldNot haveErrorType(errorType)
fun haveErrorType(errorType: EventErrorType) = object : Matcher<ResponseEvent> {
    override fun test(value: ResponseEvent) = MatcherResult(
        value.getErrorType() == errorType,
        "Event should have error type $errorType, but was ${value.getErrorType()}",
        "Event should not have error type $errorType, but does."
    )
}
