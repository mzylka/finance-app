package com.example.financeapp.data

import kotlinx.serialization.Serializable
import java.util.Date

enum class TransactionCategory {
    INCOME,
    EXPENSE,
}

@Serializable
data class Transaction(
    val id: Int,
    val date: Date,
    val description: String,
    val amount: Double,
    val category: TransactionCategory,
) {
}
