package br.com.guiabolso.events.doc.description.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocDescription(
    val description: String
)