package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.MapDescription
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.reflect.full.memberProperties

@Suppress("MapGetWithNotNullAssertionOperator")
class MapValueVisitorTest {

    @Test
    fun `should correctly classify properties as map correctly`() {
        val delegate = mock(KPropertyVisitor::class.java)
        val visitor = MapValueVisitor(CompositeKPropertyVisitor().apply { register(delegate) })
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertTrue(visitor.accept(properties["map"]!!))
        assertTrue(visitor.accept(properties["map2"]!!))
        assertTrue(visitor.accept(properties["map3"]!!))

        assertFalse(visitor.accept(properties["name"]!!))
        assertFalse(visitor.accept(properties["list"]!!))
    }

    @Test
    fun `should generate descriptions correctly`() {
        val visitor = MapValueVisitor(CompositeKPropertyVisitor().apply {
            register(PrimitiveValueVisitor())
        })
        val context = KPropertyVisitorContext()
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertEquals(
            MapDescription(
                name = "map",
                nullable = false,
                description = null,
                valueArgument = StringDescription("value", false, null, "kotlin.Any")
            ),
            visitor.visit(context, properties["map"]!!)
        )

        assertEquals(
            MapDescription(
                name = "map2",
                nullable = false,
                description = null,
                valueArgument = NaturalNumberDescription("value", true, null, 0)
            ),
            visitor.visit(context, properties["map2"]!!)
        )

        assertEquals(
            MapDescription(
                name = "map3",
                nullable = true,
                description = "One last map",
                valueArgument = StringDescription("value", false, null, "String")
            ),
            visitor.visit(context, properties["map3"]!!)
        )
    }


    @Suppress("ArrayInDataClass")
    private data class SomeVO(
        val name: String,
        val list: List<Double>,
        val map: Map<String, Any>,
        val map2: Map<String, Int?>,
        @DocDescription("One last map")
        val map3: Map<String, String>?
    )

}