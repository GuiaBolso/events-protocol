package br.com.guiabolso.events.doc.description

import br.com.guiabolso.events.doc.description.annotations.DocBooleanElement
import br.com.guiabolso.events.doc.description.annotations.DocJsonElement
import br.com.guiabolso.events.doc.description.annotations.DocNaturalNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocRealNumberElement
import br.com.guiabolso.events.doc.description.annotations.DocStringElement
import br.com.guiabolso.events.doc.description.annotations.DocValueElement
import br.com.guiabolso.events.doc.description.models.BooleanDescription
import br.com.guiabolso.events.doc.description.models.JsonDescription
import br.com.guiabolso.events.doc.description.models.NaturalNumberDescription
import br.com.guiabolso.events.doc.description.models.PrimitiveElementDescription
import br.com.guiabolso.events.doc.description.models.RealNumberDescription
import br.com.guiabolso.events.doc.description.models.StringDescription
import br.com.guiabolso.events.doc.utils.hasAnnotation
import br.com.guiabolso.events.json.MapperHolder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

object PrimitiveValueDescriber {

    fun describe(property: KProperty<*>): PrimitiveElementDescription<*> = when {
        isBoolean(property) -> BooleanDescription(property.name, getBooleanExample(property))
        isString(property) -> StringDescription(property.name, getStringExample(property))
        isRealNumber(property) -> RealNumberDescription(property.name, getRealNumberExample(property))
        isNaturalNumber(property) -> NaturalNumberDescription(property.name, getNaturalNumberExample(property))
        isJsonValue(property) -> JsonDescription(property.name, getJsonExample(property))
        isValue(property) -> StringDescription(property.name, property.toKClass().simpleName ?: "anonymous object")
        property.returnType.classifier == Any::class -> StringDescription(property.name, "Any valid json type")
        else -> throw IllegalArgumentException()
    }

    fun isPrimitiveValue(property: KProperty<*>) = when {
        isBoolean(property) -> true
        isString(property) -> true
        isNaturalNumber(property) || isRealNumber(property) -> true
        isValue(property) -> true
        property.returnType.classifier == Any::class -> true
        else -> false
    }

    private fun isBoolean(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()

        return classifier == Boolean::class ||
                property.hasAnnotation(DocBooleanElement::class) ||
                classifier.hasAnnotation(DocBooleanElement::class)
    }

    private fun isString(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()

        return classifier == String::class ||
                property.hasAnnotation(DocStringElement::class) ||
                classifier.hasAnnotation(DocStringElement::class)
    }

    private fun isNaturalNumber(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()

        return classifier.isSubclassOf(Number::class) ||
                property.hasAnnotation(DocNaturalNumberElement::class) ||
                property.hasAnnotation(DocRealNumberElement::class)
    }

    private fun isRealNumber(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()

        return classifier.isSubclassOf(Float::class) ||
                classifier.isSubclassOf(Double::class) ||
                property.hasAnnotation(DocRealNumberElement::class) ||
                classifier.hasAnnotation(DocRealNumberElement::class)
    }

    private fun isJsonValue(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return property.hasAnnotation(DocJsonElement::class) ||
                classifier.hasAnnotation(DocJsonElement::class)
    }

    private fun isValue(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return property.hasAnnotation(DocValueElement::class, 2) ||
                classifier.hasAnnotation(DocValueElement::class, 2)
    }

    private fun getBooleanExample(property: KProperty<*>): Boolean {
        val classifier = property.toKClass()
        return property.findAnnotation<DocBooleanElement>()?.example
            ?: classifier.findAnnotation<DocBooleanElement>()?.example
            ?: true
    }

    private fun getStringExample(property: KProperty<*>): String {
        val classifier = property.toKClass()
        return property.findAnnotation<DocStringElement>()?.example
            ?: classifier.findAnnotation<DocStringElement>()?.example
            ?: "String"
    }

    private fun getNaturalNumberExample(property: KProperty<*>): Int {
        val classifier = property.toKClass()
        return property.findAnnotation<DocNaturalNumberElement>()?.example
            ?: classifier.findAnnotation<DocNaturalNumberElement>()?.example
            ?: 0
    }

    private fun getRealNumberExample(property: KProperty<*>): Double {
        val classifier = property.toKClass()
        return property.findAnnotation<DocRealNumberElement>()?.example
            ?: classifier.findAnnotation<DocRealNumberElement>()?.example
            ?: 0.0
    }

    private fun getJsonExample(property: KProperty<*>): JsonElement {
        val classifier = property.toKClass()
        return property.findAnnotation<DocJsonElement>()?.example?.json()
            ?: classifier.findAnnotation<DocJsonElement>()?.example?.json()
            ?: JsonObject()
    }

    private fun String.json() = MapperHolder.mapper.fromJson(this, JsonElement::class.java)

    private fun KProperty<*>.toKClass() = this.returnType.classifier as KClass<*>

}