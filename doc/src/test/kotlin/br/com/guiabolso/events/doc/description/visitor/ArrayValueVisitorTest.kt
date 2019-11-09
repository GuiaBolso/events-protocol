package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.ArrayDescription
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.reflect.full.memberProperties

@Suppress("MapGetWithNotNullAssertionOperator")
class ArrayValueVisitorTest {

    @Test
    fun `should correctly classify properties as array correctly`() {
        val delegate = mock(KPropertyVisitor::class.java)
        val visitor = ArrayValueVisitor(CompositeKPropertyVisitor().apply { register(delegate) })
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertTrue(visitor.accept(properties["list"]!!))
        assertTrue(visitor.accept(properties["set"]!!))
        assertTrue(visitor.accept(properties["array"]!!))
        assertTrue(visitor.accept(properties["array2"]!!))

        assertFalse(visitor.accept(properties["primitive"]!!))
        assertFalse(visitor.accept(properties["map"]!!))
    }

    @Test
    fun `should generate descriptions correctly`() {
        val visitor = ArrayValueVisitor(CompositeKPropertyVisitor().apply {
            register(PrimitiveValueVisitor())
        })
        val context = KPropertyVisitorContext()
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertEquals(
            ArrayDescription(
                name = "list",
                nullable = false,
                description = null,
                argument = StringDescription("argument", true, null, "String")
            ),
            visitor.visit(context, properties["list"]!!)
        )

        assertEquals(
            ArrayDescription(
                name = "set",
                nullable = false,
                description = "Just a set",
                argument = NaturalNumberDescription("argument", false, null, 0)
            ),
            visitor.visit(context, properties["set"]!!)
        )

        assertEquals(
            ArrayDescription(
                name = "array",
                nullable = false,
                description = null,
                argument = BooleanDescription("argument", false, null, true)
            ),
            visitor.visit(context, properties["array"]!!)
        )

        assertEquals(
            ArrayDescription(
                name = "array2",
                nullable = true,
                description = null,
                argument = NaturalNumberDescription("argument", false, null, 0)
            ),
            visitor.visit(context, properties["array2"]!!)
        )
    }


    @Suppress("ArrayInDataClass")
    private data class SomeVO(
        val list: List<String?>,
        @DocDescription("Just a set")
        val set: Set<Int>,
        val array: BooleanArray,
        val array2: Array<Int>?,
        val primitive: Int,
        val map: Map<String, Any>
    )

}