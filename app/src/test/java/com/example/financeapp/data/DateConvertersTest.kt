package com.example.financeapp.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.time.Instant

class DateConvertersTest {

    private val converters = DateConverters()

    @Test
    fun testFromTimestamp_nullReturnsNull() {
        assertNull(converters.fromTimestamp(null))
    }

    @Test
    fun testDateToTimestamp_nullReturnsNull() {
        assertNull(converters.dateToTimestamp(null))
    }

    @Test
    fun testFromTimestamp_convertsLongToInstant() {
        val epochMilli = 1700000000000L
        val instant = converters.fromTimestamp(epochMilli)

        assertEquals(Instant.fromEpochMilliseconds(epochMilli), instant)
    }

    @Test
    fun testDateToTimestamp_convertsInstantToLong() {
        val epochMilli = 1700000000000L
        val instant = Instant.fromEpochMilliseconds(epochMilli)

        assertEquals(epochMilli, converters.dateToTimestamp(instant))
    }

    @Test
    fun testBidirectionalConversion_preservesEpochMilli() {
        val originalTimestamp = 1710000000123L
        val instant = converters.fromTimestamp(originalTimestamp)
        val convertedBackTimestamp = converters.dateToTimestamp(instant)

        assertEquals(originalTimestamp, convertedBackTimestamp)
    }
}
