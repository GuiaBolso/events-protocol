package br.com.guiabolso.events.json

import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

object MapperHolder {

    @JvmField
    var mapper = GsonBuilder()
        .registerTypeHierarchyAdapter(LocalDate::class.java, LocalDateSerializer)
        .registerTypeHierarchyAdapter(LocalDateTime::class.java, LocalDateTimeSerializer)
        .registerTypeHierarchyAdapter(Instant::class.java, InstantSerializer)
        .serializeNulls()
        .create()!!

}