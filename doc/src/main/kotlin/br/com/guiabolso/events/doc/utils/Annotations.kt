package br.com.guiabolso.events.doc.utils

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun KAnnotatedElement.hasAnnotation(annotation: KClass<*>, depth: Int = 1): Boolean {
    if (depth <= 0 || annotations.isEmpty()) return false

    return annotations.any {
        it.annotationClass == annotation || it.annotationClass.hasAnnotation(annotation, depth - 1)
    }
}

fun KProperty<*>.hasAnnotationOnPropertyOrClass(annotation: KClass<*>, depth: Int = 1): Boolean {
    val classifier = this.toKClass()

    return this.hasAnnotation(annotation, depth) || classifier.hasAnnotation(annotation, depth)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> KProperty<*>.findAnnotationOnPropertyOrClass(annotation: KClass<T>): T? {
    val classifier = this.toKClass()
    return annotations.firstOrNull { it.annotationClass == annotation } as? T
        ?: classifier.annotations.firstOrNull { it.annotationClass == annotation } as? T
}

fun KProperty<*>.toKClass() = this.returnType.classifier as KClass<*>
