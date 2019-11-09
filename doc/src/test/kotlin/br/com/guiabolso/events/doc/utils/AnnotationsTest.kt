package br.com.guiabolso.events.doc.utils

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

@Suppress("MapGetWithNotNullAssertionOperator")
class AnnotationsTest {

    @Test
    fun `should find only first level annotations`() {
        assertTrue(SomeVO::class.hasAnnotation(DocStringElement::class))
        assertFalse(SomeVO::class.hasAnnotation(DocValueElement::class))
    }

    @Test
    fun `should also find second level annotations`() {
        assertTrue(SomeVO::class.hasAnnotation(DocStringElement::class, 2))
        assertTrue(SomeVO::class.hasAnnotation(DocValueElement::class, 2))
    }

    @Test
    fun `should find annotations on properties or on class`() {
        val properties = SomeVO::class.memberProperties.map { it.name to it }.toMap()

        assertFalse(properties["data"]!!.hasAnnotationOnPropertyOrClass(DocDescription::class))
        assertFalse(properties["test1"]!!.hasAnnotationOnPropertyOrClass(DocDescription::class))
        assertTrue(properties["test2"]!!.hasAnnotationOnPropertyOrClass(DocDescription::class))
        assertTrue(properties["test3"]!!.hasAnnotationOnPropertyOrClass(DocDescription::class))

        assertEquals("Test OtherVO", properties["test2"]!!.findAnnotationOnPropertyOrClass(DocDescription::class)?.description)
        assertEquals("Test OtherVO2", properties["test3"]!!.findAnnotationOnPropertyOrClass(DocDescription::class)?.description)
    }

    @DocStringElement
    private data class SomeVO(
        val data: String,
        val test1: OtherVO,
        @DocDescription("Test OtherVO")
        val test2: OtherVO,
        val test3: OtherVO2
    )

    private data class OtherVO(
        val age: Int
    )

    @DocDescription("Test OtherVO2")
    private data class OtherVO2(
        val age: Int
    )

}