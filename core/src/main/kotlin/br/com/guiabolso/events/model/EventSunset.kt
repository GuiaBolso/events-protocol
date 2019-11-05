package br.com.guiabolso.events.model

import java.time.LocalDateTime

data class EventSunset(
    val date: LocalDateTime,
    val description: String?
)