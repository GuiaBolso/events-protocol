package br.com.guiabolso.events.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonStringTest : StringSpec({

    "should escape especial characters" {
        val escapedString = JsonString.escape("Text with special character / \" \b \t \r \n \u000C")
        escapedString shouldBe """Text with special character / \" \b \t \r \n \f"""
    }
})
