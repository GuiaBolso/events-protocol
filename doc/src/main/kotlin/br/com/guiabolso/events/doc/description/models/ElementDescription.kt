package br.com.guiabolso.events.doc.description.models

import com.google.gson.JsonElement

sealed class ElementDescription(
    open val name: String,
    open val type: String
)

sealed class PrimitiveElementDescription<T>(
    name: String,
    type: String,
    open val example: T
) : ElementDescription(name, type)

data class BooleanDescription(
    override val name: String,
    override val example: Boolean
) : PrimitiveElementDescription<Boolean>(name, "Boolean", example)

data class StringDescription(
    override val name: String,
    override val example: String
) : PrimitiveElementDescription<String>(name, "String", example)

data class NaturalNumberDescription(
    override val name: String,
    override val example: Int
) : PrimitiveElementDescription<Int>(name, "Integer", example)

data class RealNumberDescription(
    override val name: String,
    override val example: Double
) : PrimitiveElementDescription<Double>(name, "Double", example)

data class JsonDescription(
    override val name: String,
    override val example: JsonElement
) : PrimitiveElementDescription<JsonElement>(name, "Json", example)

data class ObjectDescription(
    override val name: String,
    override val type: String,
    val properties: List<ElementDescription>
) : ElementDescription(name, type)

data class ArrayDescription(
    override val name: String,
    val classifier: ElementDescription
) : ElementDescription(name, "Array")

data class MapDescription(
    override val name: String,
    val keyClassifier: StringDescription,
    val valueClassifier: ElementDescription
) : ElementDescription(name, "Map")