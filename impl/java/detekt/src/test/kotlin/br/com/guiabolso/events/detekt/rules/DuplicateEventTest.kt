package br.com.guiabolso.events.detekt.rules

import io.gitlab.arturbosch.detekt.test.lint
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize

class DuplicateEventTest : FunSpec({

    test("Detects defect in multiple handlers") {

        val code =
            """
    class MyEvent1 : EventHandler {
        override val eventName: String = "abc"
        override val eventVersion: Int = 1
        
        override fun handle(event: RequestEvent): ResponseEvent {
            throw RuntimeException()
        }
    }
    
    class MyEvent2 : EventHandler {
        override val eventName: String = "abc"
        override val eventVersion: Int = 1
        
        override fun handle(event: RequestEvent): ResponseEvent {
            throw RuntimeException()
        }
    }
"""

        DuplicateEvent().lint(code) shouldHaveSize 1
    }

    test("Detects defekt with KTor Events") {
        val code =
            """
        fun Application.testModule() {
            events {
                event("test:event", 1) {
                    responseFor(it) {
                        payload = mapOf("answer" to 42)
                    }
                }

                event(name = "test:event", version = 1) {
                    errorFor(it, BadRequest, EventMessage("SOME_ERROR", emptyMap()))
                }
            }
        }
        """

        DuplicateEvent().lint(code) shouldHaveSize 1
    }
})
