package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.BaseObjectReferenceDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.ObjectDescription
import br.com.guiabolso.events.doc.description.models.ObjectReferenceDescription
import br.com.guiabolso.events.doc.utils.findAnnotationOnPropertyOrClass
import br.com.guiabolso.events.doc.utils.toKClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties

class ObjectValueVisitor(
    private val delegate: CompositeKPropertyVisitor
) : KPropertyVisitor<BaseObjectReferenceDescription> {

    override fun accept(property: KProperty<*>) = true

    override fun visit(context: KPropertyVisitorContext, property: KProperty<*>): BaseObjectReferenceDescription {
        val name = property.name
        val classifier = property.toKClass()
        val nullable = property.returnType.isMarkedNullable
        val description = getDescription(property)

        val identity = classifier.identity()
        return if (context.insideOf(identity)) {
            ObjectReferenceDescription(name, identity, nullable, description)
        } else {
            val newContext = context.add(identity)
            val properties = classifier.memberProperties.map { delegate.visit(newContext, it) }
            ObjectDescription(name, identity, nullable, description, properties)
        }
    }

    private fun getDescription(property: KProperty<*>): String? {
        return property.findAnnotationOnPropertyOrClass(DocDescription::class)?.description
    }

}