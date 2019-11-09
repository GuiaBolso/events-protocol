package br.com.guiabolso.events.doc.description.visitor

import br.com.guiabolso.events.doc.description.annotations.DocBooleanElement
import br.com.guiabolso.events.doc.description.annotations.DocDescription
import br.com.guiabolso.events.doc.description.annotations.DocJsonElement
import br.com.guiabolso.events.doc.description.annotations.DocNaturalNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocRealNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.JsonDescription
import br.com.guiabolso.events.doc.description.models.KPropertyVisitorContext
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.PrimitiveElementDescription
import br.com.guiabolso.events.doc.description.models.RealNumberDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import br.com.guiabolso.events.doc.utils.findAnnotationOnPropertyOrClass
import br.com.guiabolso.events.doc.utils.hasAnnotationOnPropertyOrClass
import br.com.guiabolso.events.doc.utils.toKClass
import br.com.guiabolso.events.json.MapperHolder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubclassOf

class PrimitiveValueVisitor : KPropertyVisitor<PrimitiveElementDescription<*>> {

    private val buildInTypes = listOf(
        Regex("^java\\.time\\..*$"),
        Regex("^java\\.lang\\..*$"),
        Regex("^kotlin.Any$")
    )

    override fun accept(property: KProperty<*>) = when {
        isBoolean(property) -> true
        isString(property) -> true
        isNaturalNumber(property) -> true
        isRealNumber(property) -> true
        isValue(property) -> true
        isBuiltIn(property) -> true
        property.returnType.classifier == Any::class -> true
        else -> false
    }

    override fun visit(context: KPropertyVisitorContext, property: KProperty<*>): PrimitiveElementDescription<*> {
        val name = property.name
        val nullable = property.returnType.isMarkedNullable
        val description = getDescription(property)
        return when {
            isBoolean(property) ->
                BooleanDescription(name, nullable, description, getBooleanExample(property))
            isString(property) ->
                StringDescription(name, nullable, description, getStringExample(property))
            isRealNumber(property) ->
                RealNumberDescription(name, nullable, description, getRealNumberExample(property))
            isNaturalNumber(property) ->
                NaturalNumberDescription(name, nullable, description, getNaturalNumberExample(property))
            isJsonValue(property) ->
                JsonDescription(name, nullable, description, getJsonExample(property))
            isValue(property) || isBuiltIn(property) ->
                StringDescription(name, nullable, description, property.toKClass().identity())
            else -> throw IllegalArgumentException()
        }
    }

    private fun isBoolean(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return classifier == Boolean::class ||
            property.hasAnnotationOnPropertyOrClass(DocBooleanElement::class)
    }

    private fun isString(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return classifier == String::class ||
            property.hasAnnotationOnPropertyOrClass(DocStringElement::class)
    }

    private fun isNaturalNumber(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return classifier.isSubclassOf(Number::class) ||
            property.hasAnnotationOnPropertyOrClass(DocNaturalNumberElement::class)
    }

    private fun isRealNumber(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return classifier.isSubclassOf(Float::class) ||
            classifier.isSubclassOf(Double::class) ||
            property.hasAnnotationOnPropertyOrClass(DocRealNumberElement::class)
    }

    private fun isJsonValue(property: KProperty<*>): Boolean {
        return property.hasAnnotationOnPropertyOrClass(DocJsonElement::class)
    }

    private fun isValue(property: KProperty<*>): Boolean {
        return property.hasAnnotationOnPropertyOrClass(DocValueElement::class, 2)
    }

    private fun isBuiltIn(property: KProperty<*>): Boolean {
        val qualifiedName = property.toKClass().identity()
        return buildInTypes.any { it.matches(qualifiedName) }
    }

    private fun getDescription(property: KProperty<*>): String? {
        return property.findAnnotationOnPropertyOrClass(DocDescription::class)?.description
    }

    private fun getBooleanExample(property: KProperty<*>): Boolean {
        return property.findAnnotationOnPropertyOrClass(DocBooleanElement::class)?.example ?: true
    }

    private fun getStringExample(property: KProperty<*>): String {
        return property.findAnnotationOnPropertyOrClass(DocStringElement::class)?.example ?: "String"
    }

    private fun getNaturalNumberExample(property: KProperty<*>): Int {
        return property.findAnnotationOnPropertyOrClass(DocNaturalNumberElement::class)?.example ?: 0
    }

    private fun getRealNumberExample(property: KProperty<*>): Double {
        return property.findAnnotationOnPropertyOrClass(DocRealNumberElement::class)?.example ?: 0.0
    }

    private fun getJsonExample(property: KProperty<*>): JsonElement {
        return property.findAnnotationOnPropertyOrClass(DocJsonElement::class)?.example?.json() ?: JsonObject()
    }

    private fun String.json() = MapperHolder.mapper.fromJson(this, JsonElement::class.java)

}