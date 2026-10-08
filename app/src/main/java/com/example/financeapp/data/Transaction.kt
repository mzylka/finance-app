package com.example.financeapp.data

enum class TransactionCategory {
    INCOME,
    EXPENSE,
}

data class Transaction(
    val id: Int,
    val date: String,
    val description: String,
    val amount: Double,
    val category: TransactionCategory,
)