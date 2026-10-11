package com.example.financeapp.data.transactions

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.financeapp.data.TransactionType
import java.math.BigDecimal
import kotlin.time.Instant

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val date: Instant,
    val categoryId: Int,
    val amount: BigDecimal,
    val type: TransactionType,
) {
    val signedAmount: BigDecimal
        get() = if (type == TransactionType.INCOME) amount else -amount
}