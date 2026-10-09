package com.example.financeapp.data

enum class TransactionType {
    INCOME,
    EXPENSE,
}

data class TransactionT(
    val id: Int,
    val date: String,
    val description: String,
    val amount: Double,
    val category: TransactionType,
)