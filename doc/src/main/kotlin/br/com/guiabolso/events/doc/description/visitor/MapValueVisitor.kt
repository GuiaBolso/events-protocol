package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.KPropertyWrapper
import br.com.guiabolso.events.doc.description.models.MapDescription
import br.com.guiabolso.events.doc.utils.findAnnotationOnPropertyOrClass
import br.com.guiabolso.events.doc.utils.toKClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class MapValueVisitor(
    private val delegate: CompositeKPropertyVisitor
) : KPropertyVisitor<MapDescription> {

    override fun accept(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return when {
            classifier.isSubclassOf(Map::class) -> true
            else -> false
        }
    }

    override fun visit(context: KPropertyVisitorContext, property: KProperty<*>): MapDescription {
        val name = property.name
        val nullable = property.returnType.isMarkedNullable
        val argumentType = property.returnType.arguments[1].type!!
        val argumentDescription = delegate.visit(context, KPropertyWrapper<Any>("value", argumentType))
        val description = getDescription(property)
        return MapDescription(name, nullable, description, argumentDescription)
    }

    private fun getDescription(property: KProperty<*>): String? {
        return property.findAnnotationOnPropertyOrClass(DocDescription::class)?.description
    }

}