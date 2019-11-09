package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.KPropertyWrapper
import br.com.guiabolso.events.doc.description.models.ObjectDescription
import br.com.guiabolso.events.doc.description.models.ObjectReferenceDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import kotlin.reflect.full.createType

@Suppress("MapGetWithNotNullAssertionOperator")
class ObjectValueVisitorTest {

    @Test
    fun `should correctly classify everything as object`() {
        val delegate = mock(KPropertyVisitor::class.java)
        val visitor = ObjectValueVisitor(CompositeKPropertyVisitor().apply { register(delegate) })

        val payload = KPropertyWrapper<Any>("payload", SomeVO::class.createType())

        assertTrue(visitor.accept(payload))
    }

    @Test
    fun `should generate descriptions correctly`() {
        val visitor = ObjectValueVisitor(CompositeKPropertyVisitor().apply {
            register(PrimitiveValueVisitor())
        })
        val context = KPropertyVisitorContext()
        val payload = KPropertyWrapper<Any>("payload", SomeVO::class.createType())

        assertEquals(
            ObjectDescription(
                name = "payload",
                type = "br.com.guiabolso.events.doc.description.visitor.ObjectValueVisitorTest.SomeVO",
                nullable = false,
                description = null,
                properties = listOf(
                    StringDescription("data", false, null, "String")
                )
            ),
            visitor.visit(context, payload)
        )
    }

    @Test
    fun `should detects cycles and avoid stack overflow`() {
        val composite = CompositeKPropertyVisitor()
        val visitor = ObjectValueVisitor(composite)

        composite.apply {
            register(PrimitiveValueVisitor())
            register(visitor)
        }

        val context = KPropertyVisitorContext()
        val payload = KPropertyWrapper<Any>("payload", SomeNodeVO::class.createType())

        assertEquals(
            ObjectDescription(
                name = "payload",
                type = "br.com.guiabolso.events.doc.description.visitor.ObjectValueVisitorTest.SomeNodeVO",
                nullable = false,
                description = "Mind the tree",
                properties = listOf(
                    ObjectReferenceDescription(
                        name = "child",
                        type = "br.com.guiabolso.events.doc.description.visitor.ObjectValueVisitorTest.SomeNodeVO",
                        nullable = true,
                        description = "Mind the tree"
                    )
                )
            ),
            visitor.visit(context, payload)
        )
    }


    private data class SomeVO(
        val data: String
    )

    @DocDescription("Mind the tree")
    private data class SomeNodeVO(
        val child: SomeNodeVO?
    )

}