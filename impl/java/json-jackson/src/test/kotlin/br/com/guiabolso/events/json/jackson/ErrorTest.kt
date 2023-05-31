package br.com.guiabolso.events.json.jackson

import br.com.guiabolso.events.json.JsonDataException
import com.fasterxml.jackson.databind.json.JsonMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk

class ErrorTest : StringSpec({

    "wraps known exceptions with JsonDataException" {

        val jsonStringSyntaxProblem = """
            {
             "list": ["abc"],
             "string": "ola",
             "int": 42,
             "boolean": true,
             "map": { "some": "thing" },
             "any": { "same": "thing" 
            }
        """.trimIndent()

        val jsonStringTypeProblem = """
            {
             "list": ["abc"],
             "string": "ola",
             "int": 42,
             "boolean": true,
             "map": 42,
             "any": { "same": "thing" }
            }
        """.trimIndent()

        val jsonStringNull = """
            {
             "list": ["abc"],
             "string": "ola",
             "int": 42,
             "boolean": true,
             "map": null,
             "any": { "same": "thing" }
            }
        """.trimIndent()

        shouldThrow<JsonDataException> {
            testAdapter.fromJson(jsonStringSyntaxProblem, Sample::class.java)
        }

        shouldThrow<JsonDataException> {
            testAdapter.fromJson(jsonStringTypeProblem, Sample::class.java)
        }

        shouldThrow<JsonDataException> {
            testAdapter.fromJson(jsonStringNull, Sample::class.java)
        }
    }

    "rethrows unknown exceptions" {
        val mapper = mockk<JsonMapper>()
        val adapterWithMockMapper = Jackson2JsonAdapter(mapper)
        every { mapper.readValue(any<String>(), any<Class<*>>()) }
            .throws(NullPointerException())

        val jsonStringValid = """
            {
             "list": ["abc"],
             "string": "ola",
             "int": 42,
             "boolean": true,
             "map": { "some": "thing" },
             "any": { "same": "thing" }
            }
        """.trimIndent()

        shouldThrow<NullPointerException> {
            adapterWithMockMapper.fromJson(jsonStringValid, Sample::class.java)
        }
    }
})
