package br.com.guiabolso.events.json

import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

object MapperHolder {

    @JvmField
    var mapper = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer)
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
        .registerTypeAdapter(Instant::class.java, InstantSerializer)
        .serializeNulls()
        .create()!!

}