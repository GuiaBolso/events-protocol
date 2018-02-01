package br.com.guiabolso.events.json

import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object MapperHolder {

    @JvmField
    var mapper = GsonBuilder()
            .serializeNulls()
            .registerTypeHierarchyAdapter(LocalDate::class.java, LocalDateSerializer)
            .registerTypeHierarchyAdapter(LocalDateTime::class.java, LocalDateTimeSerializer(ZoneId.systemDefault()))
            .create()!!

}