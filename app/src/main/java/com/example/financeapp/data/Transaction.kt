package com.example.financeapp.data

import java.util.Date

enum class TransactionCategory {
    INCOME,
    EXPENSE,
}

data class Transaction(
    val id: Int,
    val date: Date,
    val description: String,
    val amount: Double,
    val category: TransactionCategory,
)