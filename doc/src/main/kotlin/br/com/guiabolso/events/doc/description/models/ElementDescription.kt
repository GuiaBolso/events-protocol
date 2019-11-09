package br.com.guiabolso.events.doc.description.models

import com.google.gson.JsonElement

sealed class ElementDescription() {
    abstract val name: String
    abstract val type: String
    abstract val nullable: Boolean
    abstract val description: String?
}

sealed class PrimitiveElementDescription<T> : ElementDescription() {
    abstract val example: T
}

data class BooleanDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Boolean
) : PrimitiveElementDescription<Boolean>() {
    override val type = "Boolean"
}

data class StringDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: String
) : PrimitiveElementDescription<String>() {
    override val type = "String"
}

data class NaturalNumberDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Int
) : PrimitiveElementDescription<Int>() {
    override val type = "Integer"
}

data class RealNumberDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: Double
) : PrimitiveElementDescription<Double>() {
    override val type = "Double"
}

data class JsonDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    override val example: JsonElement
) : PrimitiveElementDescription<JsonElement>() {
    override val type = "Json"
}

sealed class BaseObjectReferenceDescription() : ElementDescription()

data class ObjectReferenceDescription(
    override val name: String,
    override val type: String,
    override val nullable: Boolean,
    override val description: String?
) : BaseObjectReferenceDescription()

data class ObjectDescription(
    override val name: String,
    override val type: String,
    override val nullable: Boolean,
    override val description: String?,
    val properties: List<ElementDescription>
) : BaseObjectReferenceDescription()

data class ArrayDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    val argument: ElementDescription
) : ElementDescription() {
    override val type = "Array"
}

data class MapDescription(
    override val name: String,
    override val nullable: Boolean,
    override val description: String?,
    val valueArgument: ElementDescription
) : ElementDescription() {
    override val type = "Map"
}