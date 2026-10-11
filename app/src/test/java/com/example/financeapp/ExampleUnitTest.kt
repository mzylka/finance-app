package com.example.financeapp

import com.example.financeapp.data.MoneyConverter
import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal

class MoneyConverterTest {

    private val converter = MoneyConverter()

    @Test
    fun testFromBigDecimal_nullReturnsNull() {
        assertNull(converter.fromBigDecimal(null))
    }

    @Test
    fun testToBigDecimal_nullReturnsNull() {
        assertNull(converter.toBigDecimal(null))
    }

    @Test
    fun testFromBigDecimal_exactTwoDecimals() {
        assertEquals(1050L, converter.fromBigDecimal(BigDecimal("10.50")))
        assertEquals(1000L, converter.fromBigDecimal(BigDecimal("10.00")))
        assertEquals(0L, converter.fromBigDecimal(BigDecimal("0.00")))
    }

    @Test
    fun testFromBigDecimal_moreThanTwoDecimalsRoundsCorrectly() {
        // 10.555 -> 1055.5 cents -> rounded HALF_UP to 1056 cents
        assertEquals(1056L, converter.fromBigDecimal(BigDecimal("10.555")))
        // 10.554 -> 1055.4 cents -> rounded HALF_UP to 1055 cents
        assertEquals(1055L, converter.fromBigDecimal(BigDecimal("10.554")))
    }

    @Test
    fun testFromBigDecimal_doubleConstructionDoesNotThrowArithmeticException() {
        val bdFromDouble = BigDecimal(10.55) // Has binary floating point inaccuracies
        val result = converter.fromBigDecimal(bdFromDouble)
        assertEquals(1055L, result)
    }

    @Test
    fun testToBigDecimal_convertsLongToBigDecimalWithTwoDecimals() {
        assertEquals(BigDecimal("10.50"), converter.toBigDecimal(1050L))
        assertEquals(BigDecimal("10.00"), converter.toBigDecimal(1000L))
        assertEquals(BigDecimal("0.00"), converter.toBigDecimal(0L))
    }
}