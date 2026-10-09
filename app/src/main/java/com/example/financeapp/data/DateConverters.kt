package com.example.financeapp.data

import androidx.room.TypeConverter
import kotlin.time.Instant

class DateConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun dateToTimestamp(instant: Instant?): Long? {
        return instant?.toEpochMilliseconds()
    }
}