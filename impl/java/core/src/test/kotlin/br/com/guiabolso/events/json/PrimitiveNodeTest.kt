package br.com.guiabolso.events.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PrimitiveNodeTest : StringSpec({

    "toString should generate a valid json string" {
        PrimitiveNode(42).toString() shouldBe "42"
        PrimitiveNode(42.42).toString() shouldBe "42.42"
        PrimitiveNode(true).toString() shouldBe "true"
        PrimitiveNode("string").toString() shouldBe "\"string\""
        JsonNull.toString() shouldBe "null"
        val escapedString = PrimitiveNode("Text with special character / \" \b \t \r \n \u000C").toString()
        escapedString shouldBe """"Text with special character / \" \b \t \r \n \f""""
    }
})
