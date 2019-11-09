package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocBooleanElement
import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.annotations.DocJsonElement
import br.com.guiabolso.events.doc.description.annotations.DocNaturalNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.JsonDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.RealNumberDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.full.memberProperties

@Suppress("MapGetWithNotNullAssertionOperator")
class PrimitiveValueVisitorTest {

    @Test
    fun `should correctly classify properties as values correctly`() {
        val visitor = PrimitiveValueVisitor()
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertTrue(visitor.accept(properties["name"]!!))
        assertTrue(visitor.accept(properties["lastName"]!!))
        assertTrue(visitor.accept(properties["enabled"]!!))
        assertTrue(visitor.accept(properties["deleted"]!!))
        assertTrue(visitor.accept(properties["age"]!!))
        assertTrue(visitor.accept(properties["height"]!!))
        assertTrue(visitor.accept(properties["otherVO"]!!))
        assertTrue(visitor.accept(properties["timestamp"]!!))
        assertTrue(visitor.accept(properties["date"]!!))
        assertTrue(visitor.accept(properties["date2"]!!))
        assertTrue(visitor.accept(properties["anything"]!!))

        assertFalse(visitor.accept(properties["map"]!!))
        assertFalse(visitor.accept(properties["list"]!!))
        assertFalse(visitor.accept(properties["array"]!!))
    }

    @Test
    fun `should generate descriptions correctly`() {
        val visitor = PrimitiveValueVisitor()
        val context = KPropertyVisitorContext()
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertEquals(StringDescription("name", false, null, "José"), visitor.visit(context, properties["name"]!!))
        assertEquals(StringDescription("lastName", false, null, "String"), visitor.visit(context, properties["lastName"]!!))
        assertEquals(BooleanDescription("enabled", false, null, true), visitor.visit(context, properties["enabled"]!!))
        assertEquals(BooleanDescription("deleted", false, null, false), visitor.visit(context, properties["deleted"]!!))
        assertEquals(NaturalNumberDescription("age", false, null, 0), visitor.visit(context, properties["age"]!!))
        assertEquals(RealNumberDescription("height", true, null, 0.0), visitor.visit(context, properties["height"]!!))
        val json = JsonObject().apply { addProperty("potato", "please") }
        assertEquals(JsonDescription("otherVO", false, null, json), visitor.visit(context, properties["otherVO"]!!))
        assertEquals(NaturalNumberDescription("timestamp", false, "Date in timestamp", 1573198597), visitor.visit(context, properties["timestamp"]!!))
        assertEquals(StringDescription("date", false, null, "java.time.LocalDateTime"), visitor.visit(context, properties["date"]!!))
        assertEquals(StringDescription("date2", true, null, "java.time.LocalDateTime"), visitor.visit(context, properties["date2"]!!))
        assertEquals(StringDescription("anything", false, null, "kotlin.Any"), visitor.visit(context, properties["anything"]!!))
    }


    @Suppress("ArrayInDataClass")
    private data class SomeVO(
        @DocStringElement("José")
        val name: String,
        val lastName: String,
        val enabled: Boolean,
        @DocBooleanElement(false)
        val deleted: Boolean,
        val age: Int,
        val height: Double?,
        val otherVO: OtherVO,
        @DocDescription("Date in timestamp")
        @DocNaturalNumberElement(1573198597)
        val timestamp: LocalDateTime,
        val date: LocalDateTime,
        @DocValueElement
        val date2: LocalDateTime?,
        val map: Map<String, Any>,
        val list: List<String>,
        val array: Array<String>,
        val anything: Any
    )

    @DocJsonElement("""{"potato": "please"}""")
    private data class OtherVO(
        val thisWillBeSerializedAsPotato: String
    )
}