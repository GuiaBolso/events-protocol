package br.com.guiabolso.events.model

data class EventMessage(
        val code: String,
        val parameters: Map<String, Any?>
)
