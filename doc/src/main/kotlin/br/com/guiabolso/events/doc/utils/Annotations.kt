package br.com.guiabolso.events.doc.utils

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass

fun KAnnotatedElement.hasAnnotation(annotation: KClass<*>, depth: Int = 1): Boolean {
    if (depth <= 0 || annotations.isEmpty()) return false

    return annotations.any {
        it.annotationClass == annotation || it.annotationClass.hasAnnotation(annotation, depth - 1)
    }
}