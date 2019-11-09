package br.com.guiabolso.events.doc.description

import br.com.guiabolso.events.doc.description.models.ElementDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.KPropertyWrapper
import br.com.guiabolso.events.doc.description.visitor.KPropertyVisitorFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class ObjectDescriber {

    private val visitor = KPropertyVisitorFactory.createVisitor()

    fun describe(name: String, type: KClass<*>): ElementDescription {
        val context = KPropertyVisitorContext()
        val wrapper = KPropertyWrapper<Any>(name, type.createType())
        return visitor.visit(context, wrapper)
    }

    private data class Holder<T>(
        val data:T
    )

}