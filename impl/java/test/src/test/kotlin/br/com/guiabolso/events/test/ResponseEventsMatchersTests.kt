package br.com.guiabolso.events.test

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.EventErrorType
import br.com.guiabolso.events.model.EventMessage
import br.com.guiabolso.events.model.RedirectPayload
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class ResponseEventsMatchersTests : FunSpec({
   
    val successResponse = EventBuilder.responseFor(event) { }
    val redirectResponse = EventBuilder.redirectFor(event, RedirectPayload("a"))
    val errorResponse = EventBuilder.errorFor(event, EventErrorType.BadRequest, EventMessage("code", emptyMap()))
    
    test("Should be success") {
        successResponse.shouldBeSuccess()
        redirectResponse.shouldNotBeSuccess()
        errorResponse.shouldNotBeSuccess()

        shouldThrow<AssertionError> { successResponse.shouldNotBeSuccess() }
        shouldThrow<AssertionError> { redirectResponse.shouldBeSuccess() }
        shouldThrow<AssertionError> { errorResponse.shouldBeSuccess() }
    }

    test("Should be error") {
        errorResponse.shouldBeError()
        redirectResponse.shouldNotBeError()
        successResponse.shouldNotBeError()

        shouldThrow<AssertionError> { errorResponse.shouldNotBeError() }
        shouldThrow<AssertionError> { redirectResponse.shouldBeError() }
        shouldThrow<AssertionError> { successResponse.shouldBeError() }
    }

    test("Should be redirect") {
        redirectResponse.shouldBeRedirect()
        errorResponse.shouldNotBeRedirect()
        successResponse.shouldNotBeRedirect()

        shouldThrow<AssertionError> { redirectResponse.shouldNotBeRedirect() }
        shouldThrow<AssertionError> { errorResponse.shouldBeRedirect() }
        shouldThrow<AssertionError> { successResponse.shouldBeRedirect() }
    }
    
    test("Should have error type") {
        errorResponse shouldHaveErrorType EventErrorType.BadRequest
        errorResponse shouldNotHaveErrorType EventErrorType.Expired

        shouldThrow<AssertionError> { errorResponse shouldNotHaveErrorType EventErrorType.BadRequest }
        shouldThrow<AssertionError> { errorResponse shouldHaveErrorType EventErrorType.Expired }
    }
    
})

private val event = EventBuilder.event {
    name = "a:b"
    version = 3
    id = "id"
    flowId = "flowId"
    payload = complexMap
    identity = complexMap
    auth = complexMap
    metadata = complexMap
}

private val complexMap = mapOf(
    "a" to "b",
    "c" to mapOf("a" to "b")
)
