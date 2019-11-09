package br.com.guiabolso.events.doc.description.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@DocValueElement
annotation class DocJsonElement(
    val example: String = "{}"
)