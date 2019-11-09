package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.models.ElementDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import kotlin.reflect.KProperty

class CompositeKPropertyVisitor {

    private val visitors = mutableListOf<KPropertyVisitor<*>>()

    fun visit(context: KPropertyVisitorContext, property: KProperty<*>): ElementDescription {
        val visitor = visitors.firstOrNull { it.accept(property) } ?: throw IllegalArgumentException()
        return visitor.visit(context, property)
    }

    fun register(visitor: KPropertyVisitor<*>) {
        visitors.add(visitor)
    }

}
