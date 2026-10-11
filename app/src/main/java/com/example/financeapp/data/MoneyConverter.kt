package com.example.financeapp.data

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyConverter {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): Long? {
        return value?.movePointRight(2)?.setScale(0, RoundingMode.HALF_UP)?.longValueExact()
    }

    @TypeConverter
    fun toBigDecimal(value: Long?): BigDecimal? {
        return value?.let { BigDecimal.valueOf(it).movePointLeft(2) }
    }
}