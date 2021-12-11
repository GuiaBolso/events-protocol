package br.com.guiabolso.events.validation

import br.com.guiabolso.events.MapperHolderSetup
import br.com.guiabolso.events.json.JsonNode.TreeNode
import br.com.guiabolso.events.json.MapperHolder
import br.com.guiabolso.events.json.getAsPrimitiveNumberNode
import br.com.guiabolso.events.json.getValue

import br.com.guiabolso.events.json.withCheckedJsonNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MapperHolderSetup::class)
class TypeValidationHelperTest {

    @Test
    fun testWithCheckedJsonNullWithNullInput() {

        val jsonObj = MapperHolder.mapper.fromJson(
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

        val identityJsonObj = MapperHolder.mapper.fromJson(
            """
            {
                "userId": 123987
            }
            """.trimIndent(),
            TreeNode::class.java
        )

        val userId = identityJsonObj.withCheckedJsonNull("userId") {
            it.getAsPrimitiveNumberNode("userId").value.toLong()
        }

        assertEquals(123987L, userId)
    }
}
