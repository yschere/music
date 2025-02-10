package com.example.music.data.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Room [TypeConverter] functions for various `java.time.*` classes.
 */
object DateTimeTypeConverters {
    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return value?.let { OffsetDateTime.of(LocalDateTime.parse(it, formatter), ZoneOffset.of("-0800"))}
        //return value?.let { OffsetDateTime.parse(it, formatter) }
        /* Updated this due to the songs table date_ columns using
        local date time, needed to include Zone Offset, as well as
        ofPattern formatter to properly parse into app db's
        OffsetDateTime */
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(value) }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    @TypeConverter
    @JvmStatic
    fun toDuration(value: Long?): Duration? {
        //duration = Duration.ofSeconds(236) //pulled from PreviewData.kt, used this as basis for songs table duration values
        //return value?.let { Duration.ofMillis(it) } //original code
        return value?.let { Duration.ofSeconds(it) }
    }

    @TypeConverter
    @JvmStatic
    fun fromDuration(value: Duration?): Long? {
        //return value?.toMillis() //original code
        return value?.toSeconds() //pulled from PreviewData.kt, used this as basis for songs table duration values
    }
}
