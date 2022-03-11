package br.com.guiabolso.events.json.moshi.br.com.guiabolso.events.json.moshi.factory

import br.com.guiabolso.events.json.moshi.adapter.EventProtocolAdapter
import br.com.guiabolso.events.json.moshi.factory.EventProtocolJsonAdapterFactory
import br.com.guiabolso.events.json.moshi.moshi
import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.ResponseEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

class EventProtocolJsonAdapterFactoryTest : StringSpec({

    "should return adapter for each event subclasses" {
        Event::class.sealedSubclasses.forAll {
            val adapter = EventProtocolJsonAdapterFactory.create(it.javaObjectType, emptySet(), moshi)
            adapter.shouldBeInstanceOf<EventProtocolAdapter<out Event>>()
        }
    }

    "should throw for unmapped event class" {
        shouldThrow<IllegalStateException> {
            EventProtocolJsonAdapterFactory.create(
                typeOf<Event>().javaType,
                emptySet(),
                moshi
            )
        }.shouldHaveMessage("Unmapped event protocol class br.com.guiabolso.events.model.Event")
    }
})
