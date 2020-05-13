package br.com.guiabolso.events.validation

import br.com.guiabolso.events.json.MapperHolder
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TypeValidationHelperTest {

    @Test
    fun testWithCheckedJsonNullWithNullInput() {

        val jsonObj = MapperHolder.mapper.fromJson(
            """
        {
            "userId": null
        }
        """.trimIndent(), JsonObject::class.java
        )

        val userId = jsonObj.withCheckedJsonNull("userId") {
            it.getAsJsonPrimitive("userId")
        }

        assertNull(userId)
    }

    @Test
    fun testWithCheckedJsonNullWithValidInput() {

        val identityJsonObj = MapperHolder.mapper.fromJson(
            """
            {
                "userId": 123987
            }
        """.trimIndent(), JsonObject::class.java
        )

        val userId = identityJsonObj.withCheckedJsonNull("userId") {
            it.getAsJsonPrimitive("userId").asLong
        }

        assertEquals(123987L, userId)
    }
}
