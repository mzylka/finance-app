package com.example.financeapp.data.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financeapp.data.TransactionType
import kotlin.time.Instant

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: Instant,
    val categoryId: Int,
    val amount: Double,
    val type: TransactionType,
) {
    val signedAmount: Double
        get() = if (type == TransactionType.INCOME) amount else -amount
}