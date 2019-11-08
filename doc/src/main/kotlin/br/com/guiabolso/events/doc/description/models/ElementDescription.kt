package br.com.guiabolso.events.doc.description.models

import com.google.gson.JsonElement

sealed class ElementDescription(
    open val name: String,
    open val type: String,
    open val nullable: Boolean,
    open val description: String?
)

sealed class PrimitiveElementDescription<T>(
    name: String,
    type: String,
    nullable: Boolean,
    description: String?,
    open val example: T
) : ElementDescription(name, type, nullable, description)

data class BooleanDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Boolean
) : PrimitiveElementDescription<Boolean>(name, "Boolean", nullable, description, example)

data class StringDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: String
) : PrimitiveElementDescription<String>(name, "String", nullable, description, example)

data class NaturalNumberDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Int
) : PrimitiveElementDescription<Int>(name, "Integer", nullable, description, example)

data class RealNumberDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Double
) : PrimitiveElementDescription<Double>(name, "Double", nullable, description, example)

data class JsonDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: JsonElement
) : PrimitiveElementDescription<JsonElement>(name, "Json", nullable, description, example)

data class ObjectDescription(
    override val name: String,
    override val type: String,
    override val nullable: Boolean,
    override val description: String?,
    val properties: List<ElementDescription>
) : ElementDescription(name, type, nullable, description)

data class ObjectReferenceDescription(
    override val name: String,
    override val type: String,
    override val nullable: Boolean,
    override val description: String?
) : ElementDescription(name, type, nullable, description)

data class ArrayDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    val classifier: ElementDescription
) : ElementDescription(name, "Array", nullable, description)

data class MapDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    val keyClassifier: StringDescription,
    val valueClassifier: ElementDescription
) : ElementDescription(name, "Map", nullable, description)