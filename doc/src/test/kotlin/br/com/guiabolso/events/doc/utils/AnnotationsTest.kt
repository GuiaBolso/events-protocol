package br.com.guiabolso.events.doc.utils

import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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

    @DocStringElement
    private data class SomeVO(
        val data: String
    )

}