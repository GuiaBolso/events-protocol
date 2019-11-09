package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.models.ElementDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface KPropertyVisitor<D : ElementDescription> {

    fun accept(property: KProperty<*>): Boolean

    fun visit(context: KPropertyVisitorContext, property: KProperty<*>): D

    fun KClass<*>.identity() = this.qualifiedName ?: "anonymous object"

}
