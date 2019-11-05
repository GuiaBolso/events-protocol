package br.com.guiabolso.events.json

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateSerializersTest {

    @Test
    fun `should serialize local date without time of day`() {
        val date = LocalDate.of(2019, 11, 5)

        val serialized = MapperHolder.mapper.toJson(LocalDateTest(date))
        val deserialized = MapperHolder.mapper.fromJson(serialized, LocalDateTest::class.java)

        assertEquals("{\"date\":\"2019-11-05\"}", serialized)
        assertEquals(date, deserialized.date)
    }

    @Test
    fun `should serialize local date time in UTC`() {
        val zonedDateTime = ZonedDateTime.of(LocalDateTime.of(2019, 11, 5, 12, 0, 0), ZoneId.of("America/Sao_Paulo"))
        val date = zonedDateTime.withZoneSameLocal(ZoneId.systemDefault()).toLocalDateTime()

        val serialized = MapperHolder.mapper.toJson(LocalDateTimeTest(date))
        val deserialized = MapperHolder.mapper.fromJson(serialized, LocalDateTimeTest::class.java)

        assertEquals("{\"date\":\"2019-11-05T14:00:00Z\"}", serialized)
        assertEquals(date, deserialized.date)
    }

    @Test
    fun `should serialize instant in UTC`() {
        val zonedDateTime = ZonedDateTime.of(LocalDateTime.of(2019, 11, 5, 12, 0, 0), ZoneId.of("America/Sao_Paulo"))
        val instant = zonedDateTime.toInstant()

        val serialized = MapperHolder.mapper.toJson(InstantTest(instant))
        val deserialized = MapperHolder.mapper.fromJson(serialized, InstantTest::class.java)

        assertEquals("{\"instant\":\"2019-11-05T14:00:00Z\"}", serialized)
        assertEquals(instant, deserialized.instant)
    }

    private data class LocalDateTest(val date: LocalDate)

    private data class LocalDateTimeTest(val date: LocalDateTime)

    private data class InstantTest(val instant: Instant)

}