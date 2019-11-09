package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.models.ElementDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.KPropertyWrapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType

class CompositeKPropertyVisitor {

    private val visitors = mutableListOf<KPropertyVisitor<*>>()

    fun describe(name: String, type: KClass<*>) {
        val context = KPropertyVisitorContext()
        val wrapper = KPropertyWrapper<Any>(name, type.createType())
        visit(context, wrapper)
    }

    fun visit(context: KPropertyVisitorContext, property: KProperty<*>): ElementDescription {
        val visitor = visitors.firstOrNull { it.accept(property) } ?: throw IllegalArgumentException()
        return visitor.visit(context, property)
    }

    fun register(visitor: KPropertyVisitor<*>) {
        visitors.add(visitor)
    }

}
