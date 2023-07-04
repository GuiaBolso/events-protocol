package br.com.guiabolso.events.validation

import br.com.guiabolso.events.json.JsonAdapterProducer.mapper
import br.com.guiabolso.events.json.TreeNode
import br.com.guiabolso.events.json.long
import br.com.guiabolso.events.json.primitiveNode
import br.com.guiabolso.events.json.withCheckedJsonNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TypeValidationHelperTest {

    @Test
    fun testWithCheckedJsonNullWithNullInput() {

        val jsonObj = mapper.fromJson(
            """
        {
            "userId": null
        }
            """.trimIndent(),
            TreeNode::class.java
        )

        val userId = jsonObj.withCheckedJsonNull("userId") {
            error("Should never be called")
        }

        assertNull(userId)
    }

    @Test
    fun testWithCheckedJsonNullWithValidInput() {

        val identityJsonObj = mapper.fromJson(
            """
            {
                "userId": 123987
            }
            """.trimIndent(),
            TreeNode::class.java
        )

        val userId = identityJsonObj.withCheckedJsonNull("userId") {
            it["userId"]?.primitiveNode?.long
        }

        assertEquals(123987L, userId)
    }
}
