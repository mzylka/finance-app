package com.example.financeapp.data

import java.math.BigDecimal

data class MonthlyTotals(
    val yearMonth: String,
    val monthLabel: String,
    val income: BigDecimal = BigDecimal.ZERO,
    val expense: BigDecimal = BigDecimal.ZERO
)

data class RawMonthlyTotal(
    val yearMonth: String,
    val type: TransactionType,
    val totalAmount: BigDecimal
)
