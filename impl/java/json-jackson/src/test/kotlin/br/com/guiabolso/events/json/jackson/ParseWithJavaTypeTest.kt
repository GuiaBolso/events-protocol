package br.com.guiabolso.events.json.jackson

import com.fasterxml.jackson.core.type.TypeReference
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import java.lang.reflect.Type

class ParseWithJavaTypeTest : StringSpec({

    "can parse using ${Type::class.java}" {

        val type = object : TypeReference<Sample>() {}.type

        val jsonString = """
            {
             "list": ["abc"],
             "string": "ola",
             "int": 42,
             "boolean": true,
             "map": { "some": "thing" },
             "any": { "same": "thing" }
            }
        """.trimIndent()

        val result = testAdapter.fromJson<Sample>(jsonString, type)

        result.list.shouldContainInOrder("abc")
        result.string shouldBe "ola"
        result.int shouldBe 42
        result.boolean.shouldBeTrue()
        result.map["some"] shouldBe "thing"
        (result.any as Map<String, Any>)["same"] shouldBe "thing"
    }
})
