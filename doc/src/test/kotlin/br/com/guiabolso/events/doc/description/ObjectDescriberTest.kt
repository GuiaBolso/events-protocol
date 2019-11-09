package br.com.guiabolso.events.doc.description

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import com.google.gson.JsonObject
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId

@Suppress("ClassName", "ArrayInDataClass")
class ObjectDescriberTest {

    @Test
    fun `should be able to describe this`() {
        val describer = ObjectDescriber()

        val description = describer.describe("payload", `VO do Josine`::class)
        TODO()
    }

    private abstract class Base {
        @DocStringElement(example = "Id da casa")
        abstract val zoneId: ZoneId
        abstract val json: JsonObject
        protected lateinit var someValue: Class<*>
    }

    private data class `VO do Josine`(
        private val someMap: LinkedHashMap<String?, Instant?>,
        @DocDescription(description = "Some Set")
        private val someSet: Set<*>,
        private val someArray: Array<String>?,
        private val voDoJosine: `VO do Josine`,
        private val outroVoDoJosine: `Outro VO do Josine`
    ) {
        private val otherMap: Map<String?, Instant?> by lazy { someMap }
    }

    private data class `Outro VO do Josine`(
        override val zoneId: ZoneId,
        override val json: JsonObject,
        private val voDoJosine: `VO do Josine`?
    ) : Base()

}