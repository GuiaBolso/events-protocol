package br.com.guiabolso.events.json

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeSerializer(private val zoneId: ZoneId) : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(ZonedDateTime.of(src, zoneId).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        val zonedDateTime = ZonedDateTime.parse(json.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return LocalDateTime.ofInstant(zonedDateTime.toInstant(), zoneId)
    }

}