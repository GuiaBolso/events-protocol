package br.com.guiabolso.events.doc.description.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@DocValueElement
annotation class DocNaturalNumberElement(
    val example: Int = 0
)