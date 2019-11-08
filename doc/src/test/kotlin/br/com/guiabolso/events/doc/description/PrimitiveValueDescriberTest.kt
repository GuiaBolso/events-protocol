package br.com.guiabolso.events.doc.description

import br.com.guiabolso.events.doc.description.PrimitiveValueDescriber.describe
import br.com.guiabolso.events.doc.description.PrimitiveValueDescriber.isPrimitiveValue
import br.com.guiabolso.events.doc.description.annotations.DocBooleanElement
import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.annotations.DocJsonElement
import br.com.guiabolso.events.doc.description.annotations.DocNaturalNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.JsonDescription
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
class PrimitiveValueDescriberTest {

    @Test
    fun `should correctly classify properties as values correctly`() {
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertTrue(isPrimitiveValue(properties["name"]!!))
        assertTrue(isPrimitiveValue(properties["lastName"]!!))
        assertTrue(isPrimitiveValue(properties["enabled"]!!))
        assertTrue(isPrimitiveValue(properties["deleted"]!!))
        assertTrue(isPrimitiveValue(properties["age"]!!))
        assertTrue(isPrimitiveValue(properties["height"]!!))
        assertTrue(isPrimitiveValue(properties["otherVO"]!!))
        assertTrue(isPrimitiveValue(properties["timestamp"]!!))
        assertTrue(isPrimitiveValue(properties["date2"]!!))
        assertTrue(isPrimitiveValue(properties["anything"]!!))

        assertFalse(isPrimitiveValue(properties["date"]!!))
        assertFalse(isPrimitiveValue(properties["map"]!!))
        assertFalse(isPrimitiveValue(properties["list"]!!))
        assertFalse(isPrimitiveValue(properties["array"]!!))
    }

    @Test
    fun `should correctly generate examples to properties correctly`() {
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertEquals(StringDescription("name", false, null, "José"), describe(properties["name"]!!))
        assertEquals(StringDescription("lastName", false, null, "String"), describe(properties["lastName"]!!))
        assertEquals(BooleanDescription("enabled", false, null, true), describe(properties["enabled"]!!))
        assertEquals(BooleanDescription("deleted", false, null, false), describe(properties["deleted"]!!))
        assertEquals(NaturalNumberDescription("age", false, null, 0), describe(properties["age"]!!))
        assertEquals(RealNumberDescription("height", true, null, 0.0), describe(properties["height"]!!))
        val json = JsonObject().apply { addProperty("potato", "please") }
        assertEquals(JsonDescription("otherVO", false, null, json), describe(properties["otherVO"]!!))
        assertEquals(NaturalNumberDescription("timestamp", false, "Date in timestamp", 1573198597), describe(properties["timestamp"]!!))
        assertEquals(StringDescription("date2", true, null, "LocalDateTime"), describe(properties["date2"]!!))
        assertEquals(StringDescription("anything", false, null, "Any valid json type"), describe(properties["anything"]!!))
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