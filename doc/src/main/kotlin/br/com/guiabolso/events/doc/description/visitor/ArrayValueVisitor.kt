package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.models.ArrayDescription
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.ElementDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.KPropertyWrapper
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.RealNumberDescription
import br.com.guiabolso.events.doc.utils.findAnnotationOnPropertyOrClass
import br.com.guiabolso.events.doc.utils.toKClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class ArrayValueVisitor(
    private val delegate: CompositeKPropertyVisitor
) : KPropertyVisitor<ArrayDescription> {

    override fun accept(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return when {
            classifier.isSubclassOf(BooleanArray::class) -> true
            classifier.isSubclassOf(ByteArray::class) -> true
            classifier.isSubclassOf(ShortArray::class) -> true
            classifier.isSubclassOf(IntArray::class) -> true
            classifier.isSubclassOf(LongArray::class) -> true
            classifier.isSubclassOf(FloatArray::class) -> true
            classifier.isSubclassOf(DoubleArray::class) -> true
            classifier.isSubclassOf(Array<Any>::class) -> true
            classifier.isSubclassOf(Collection::class) -> true
            else -> false
        }
    }

    override fun visit(context: KPropertyVisitorContext, property: KProperty<*>): ArrayDescription {
        val name = property.name
        val nullable = property.returnType.isMarkedNullable
        val argumentDescription = getArgument(context, property)
        val description = getDescription(property)
        return ArrayDescription(name, nullable, description, argumentDescription)
    }

    private fun getArgument(context: KPropertyVisitorContext, property: KProperty<*>): ElementDescription {
        val classifier = property.toKClass()
        return when {
            classifier.isSubclassOf(BooleanArray::class) ->
                BooleanDescription("argument", false, null, true)
            classifier.isSubclassOf(ByteArray::class) || classifier.isSubclassOf(ShortArray::class)
                || classifier.isSubclassOf(IntArray::class) || classifier.isSubclassOf(LongArray::class) ->
                NaturalNumberDescription("argument", false, null, 0)
            classifier.isSubclassOf(FloatArray::class) || classifier.isSubclassOf(DoubleArray::class) ->
                RealNumberDescription("argument", false, null, 0.0)
            else -> {
                val argumentType = property.returnType.arguments.first().type!!
                delegate.visit(context, KPropertyWrapper<Any>("argument", argumentType))
            }
        }
    }

    private fun getDescription(property: KProperty<*>): String? {
        return property.findAnnotationOnPropertyOrClass(DocDescription::class)?.description
    }

}